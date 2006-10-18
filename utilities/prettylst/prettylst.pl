#!/usr/bin/perl

# * Copyright

# Copyright 2002 to 2006 by Éric Beaudoin <beaudoer@videotron.ca>.

# All rights reserved.  You can redistribute and/or modify
# this program under the same terms as Perl itself.

# See <http://www.perl.com/perl/misc/Artistic.html>.

# This script reads PCGEN .pcc and .lst files and reformat them to follow the directions
# given by Eddy, Tir and other list monkeys.

# See perldoc prettylst.pl for more details

use 5.008_001;                      # Perl 5.8.1 or better is now mendantory
use strict;
use warnings;
use Fatal qw( open close );         # Force some built-ins to die on error
use English qw( -no_match_vars );   # No more funky punctuation variables

# Version information               # Converting to SVN Id parsing using array - Tir Gwaith
my $SVN_id = '$Id$';
my @SVN_array = split ' ', $SVN_id;
my $SVN_build = $SVN_array[2];
my $SVN_date = $SVN_array[3];
$SVN_date =~ tr{-}{.};
my $VERSION      = "1.35 (build $SVN_build)";
my $VERSION_DATE = $SVN_date;
my ($SCRIPTNAME) = ( $PROGRAM_NAME =~ m{ ( [^/\\]* ) \z }xms );
my $VERSION_LONG = "$SCRIPTNAME version: $VERSION -- $VERSION_DATE";

my $today = localtime;

use Carp;
use Getopt::Long;
use FileHandle;
use Pod::Html      ();      # We do not import any function for
use Pod::Text      ();      # the modules other than "system" modules
use Pod::Usage     ();
use Data::Dumper   ();
use File::Find     ();
use File::Basename ();
use Text::Balanced ();

#use Data::Dump qw(dump);

# Subroutines
sub FILETYPE_parse;
sub parse_ADD_tag;
sub parse_tag;
sub validate_tag;
sub validate_pre_tag;
sub add_to_xcheck_tables;
sub extract_var_name;
sub parse_jep;
sub parse_jep_rec;
sub additionnal_tag_parsing;
sub validate_line;
sub additionnal_line_parsing;
sub additionnal_file_parsing;
sub check_clear_tag_order;
sub find_full_path;
sub get_header;
sub create_dir;
sub report_tag_sort;
sub embedded_coma_split;
sub parse_system_files;
sub warn_deprecate;
sub ewarn;
sub set_ewarn_filename;
sub set_ewarn_header;
sub record_bioset_tags;
sub generate_bioset_files;
sub generate_css;

# File handles for the Export Lists
my %filehandle_for;

# Print version information
print STDERR "$VERSION_LONG\n";

# Valid filetype are the only ones that will be parsed
# Some filetype are valid but not parse yet (no function name)
my %validfiletype = (
    'BIOSET'       => \&FILETYPE_parse,
    'CLASS'        => \&FILETYPE_parse,
    'COMPANIONMOD' => \&FILETYPE_parse,
    'DEITY'        => \&FILETYPE_parse,
    'DOMAIN'       => \&FILETYPE_parse,
    'EQUIPMENT'    => \&FILETYPE_parse,
    'EQUIPMOD'     => \&FILETYPE_parse,
    'FEAT'         => \&FILETYPE_parse,
    'INFOTEXT'     => 0,
    'KIT'          => \&FILETYPE_parse,
    'LANGUAGE'     => \&FILETYPE_parse,
    'LSTEXCLUDE'   => 0,
    'PCC'          => 1,
    'RACE'         => \&FILETYPE_parse,
    'SKILL'        => \&FILETYPE_parse,
    'SOURCELONG'   => 0,
    'SOURCESHORT'  => 0,
    'SOURCEWEB'    => 0,
    'SPELL'        => \&FILETYPE_parse,
    'TEMPLATE'     => \&FILETYPE_parse,
    'WEAPONPROF'   => \&FILETYPE_parse,
    '#EXTRAFILE'   => 1,
);

# The file type that will be rewritten.
my %writefiletype = (
    'BIOSET'       => 1,
    'CLASS'        => 1,
    'CLASS Level'  => 1,
    'COMPANIONMOD' => 1,
    'COPYRIGHT'    => 0,
    'DEITY'        => 1,
    'DOMAIN'       => 1,
    'EQUIPMENT'    => 1,
    'EQUIPMOD'     => 1,
    'FEAT'         => 1,
    'KIT',         => 1,
    'LANGUAGE'     => 1,
    'LSTEXCLUDE'   => 0,
    'INFOTEXT'     => 0,
    'PCC'          => 1,
    'RACE'         => 1,
    'SKILL'        => 1,
    'SPELL'        => 1,
    'TEMPLATE'     => 1,
    'WEAPONPROF'   => 1,
    '#EXTRAFILE'   => 0,
);

# The active conversions
my %conversion_enable = (
       'Generate BONUS and PRExxx report'   => 0,
       # After PCGEN 2.7.3
       'ALL: 4.3.3 Weapon name change'      => 0,               # Bunch of name changed for SRD compliance
       'EQUIPMENT: remove ATTACKS'          => 0,               #[ 686169 ] remove ATTACKS: tag
       'EQUIPMENT: SLOTS:2 for plurals'     => 0,               #[ 695677 ] EQUIPMENT: SLOTS for gloves, bracers and boots
       'PCC:GAME to GAMEMODE'               => 0,               #[ 707325 ] PCC: GAME is now GAMEMODE
       'ALL: , to | in VISION'              => 0,               #[ 699834 ] Incorrect loading of multiple vision types
                                                                #[ 728038 ] BONUS:VISION must replace VISION:.ADD
       'ALL:PRESTAT needs a ,'              => 0,               # PRESTAT now only accepts the format PRESTAT:1,<stat>=<n>
       'ALL:BONUS:MOVE convertion'          => 0,               #[ 711565 ] BONUS:MOVE replaced with BONUS:MOVEADD
       'ALL:PRECLASS needs a ,'             => 0,               #[ 731973 ] ALL: new PRECLASS syntax
       'ALL:COUNT[FEATTYPE=...'             => 0,               #[ 737718 ] COUNT[FEATTYPE] data change
       'ALL:Add TYPE=Base.REPLACE'          => 0,               #[ 784363 ] Add TYPE=Base.REPLACE to most BONUS:COMBAT|BAB
       'PCC:GAMEMODE DnD to 3e'             => 0,               #[ 825005 ] convert GAMEMODE:DnD to GAMEMODE:3e
       'RACE:CSKILL to MONCSKILL'           => 0,               #[ 831569 ] RACE:CSKILL to MONCSKILL
       'RACE:NoProfReq'                     => 0,               #[ 832164 ] Adding NoProfReq to AUTO:WEAPONPROF for most races
       'RACE:BONUS SKILL Climb and Swim'    => 0,               # Fix for Barak files
       'WEAPONPROF:No more SIZE'            => 0,               #[ 845853 ] SIZE is no longer valid in the weaponprof files
       'EQUIP:no more MOVE'                 => 0,               #[ 865826 ] Remove the deprecated MOVE tag in EQUIPMENT files
#       'ALL:EQMOD has new keys'             => 0,               #[ 892746 ] KEYS entries were changed in the main files
       'CLASS:CASTERLEVEL for all casters'  => 0,               #[ 876536 ] All spell casting classes need CASTERLEVEL
       'ALL:MOVE:nn to MOVE:Walk,nn'        => 0,               #[ 1006285 ] Convertion MOVE:<number> to MOVE:Walk,<Number>
       'ALL:Convert SPELL to SPELLS'        => 0,               #[ 1070084 ] Convert SPELL to SPELLS
       'TEMPLATE:HITDICESIZE to HITDIE'     => 0,               #[ 1070344 ] HITDICESIZE to HITDIE in templates.lst
       'ALL:PREALING conversion'            => 0,               #[ 1173567 ] Convert old style PREALIGN to new style
       'ALL:PRERACE needs a ,'              => 0,               #
       'ALL:Willpower to Will'              => 0,               #[ 1398237 ] ALL: Convert Willpower to Will
       'ALL:New SOURCExxx tag format'       => 1,               #[ 1444527 ] New SOURCE tag format

       'Export lists'                       => 0,               # Export various lists of entities
       'SOURCE line replacement'            => 1,
       'CLASSSKILL convertion to CLASS'     => 0,
       'CLASS:Four lines'                   => 1,               #[ 626133 ] Convert CLASS lines into 3 lines
       'ALL:Multiple lines to one'          => 0,               # Reformat multiple lines to one line for
                                                                # RACE and TEMPLATE
       'CLASSSPELL convertion to SPELL'     => 0,               #[ 641912 ] Convert CLASSSPELL to SPELL
       'SPELL:Add TYPE tags'                => 0,               #[ 653596 ] Add a TYPE tag for all SPELLs
       'BIOSET:generate the new files'      => 0,               #[ 663491 ] RACE: Convert AGE, HEIGHT and WEIGHT tags
       'EQUIPMENT: generate EQMOD'          => 0,               #[ 677962 ] The DMG wands have no charge.
       'CLASS: SPELLLIST from Spell.MOD'    => 0,               #[ 779341 ] Spell Name.MOD to CLASS's SPELLLEVEL
       'PCC:GAMEMODE Add to the CMP DnD_'   => 0,               #In order for the CMP files to work with the
                                                                #normal PCGEN files

       'ALL:Find Willpower'                 => 1,               # Find the tags that use Willpower so that we can
                                                                # plan the conversion to Will

       'ALL:CMP NatAttack fix'              => 0,               # Fix STR bonus for Natural Attacks in CMP files
       'ALL:CMP remove PREALIGN'            => 0,               # Remove the PREALIGN tag everywhere (to help my CMP friends)
);


# -------------------------------------------------------------
# Parameter parsing
# -------------------------------------------------------------

# Constants for ewarn and the warning_level parameter

use constant DEBUG   => 7;      # INFO message + debug message for the programmer
use constant INFO    => 6;      # Everything including deprecations message (default)
use constant NOTICE  => 5;      # No deprecations
use constant WARNING => 4;      # PCGEN will prabably not work properly
use constant ERROR   => 3;      # PCGEN will not work properly or the
                                # script is foobar

my %numeric_warning_level_for = (
    debug           => DEBUG,
    d               => DEBUG,
    7               => DEBUG,
    info            => INFO,
    informational   => INFO,
    i               => INFO,
    6               => INFO,
    notice          => NOTICE,
    n               => NOTICE,
    5               => NOTICE,
    warning         => WARNING,
    warn            => WARNING,
    w               => WARNING,
    4               => WARNING,
    error           => ERROR,
    err             => ERROR,
    e               => ERROR,
    3               => ERROR,
);

# Default command line options
my %cl_options = (
    basepath        => q{},     # Base path for the @ replacement
    convert         => q{},     # Activate a standard convertion
    exportlist      => 0,       # Export lists of object in CVS format
    file_type       => q{},     # File type to use if no PCC are read
    gamemode        => q{},     # GAMEMODE filter for the PCC files
    help            => 0,       # Need help? Display the usage
    html_help       => 0,       # Generate the HTML doc
    input_path      => q{},     # Path for the input directory
    man             => 0,       # Display the complete doc (man page)
    missing_header  => 0,       # Report the tags that have no defined header.
    nojep           => 0,       # Do not use the new parse_jep function
    nowarning       => 0,       # Do not display warning messages in the report
    noxcheck        => 0,       # Disable the x-check validations
    old_source_tag  => 0,       # Use | instead of \t for the SOURCExxx line
    output_error    => q{},     # Path and file name of the error log
    output_path     => q{},     # Path for the ouput directory
    report          => 0,       # Generate tag usage report
    system_path     => q{},     # Path to the system (game mode) files
    test            => 0,       # Internal, for tests only
    warning_level   => 'info',  # Warning level for error output
    xcheck          => 1,       # Perform cross-check validation
);

my $error_message = "\n";

if ( scalar @ARGV ) {
    GetOptions(
        'basepath|b=s'      => \$cl_options{ basepath       },
        'convert|c=s'       => \$cl_options{ convert        },
        'exportlist'        => \$cl_options{ exportlist     },
        'filetype|f=s'      => \$cl_options{ file_type      },
        'gamemode|gm=s'     => \$cl_options{ gamemode       },
        'help|h|?'          => \$cl_options{ help           },
        'htmlhelp'          => \$cl_options{ html_help      },
        'inputpath|i=s'     => \$cl_options{ input_path     },
        'man'               => \$cl_options{ man            },
        'missingheader|mh'  => \$cl_options{ missing_header },
        'nojep'             => \$cl_options{ nojep          },
        'nowarning|nw'      => \$cl_options{ nowarning      },
        'noxcheck|nx'       => \$cl_options{ noxcheck       },
        'old_source_tag'    => \$cl_options{ old_source_tag },
        'outputerror|e=s'   => \$cl_options{ output_error   },
        'outputpath|o=s'    => \$cl_options{ output_path    },
        'report|r'          => \$cl_options{ report         },
        'systempath|s=s'    => \$cl_options{ system_path    },
        'test'              => \$cl_options{ test           },
        'warninglevel|wl=s' => \$cl_options{ warning_level  },
        'xcheck|x'          => \$cl_options{ xcheck         },
    );

    # Print message for unknown options
    if ( scalar @ARGV ) {
        $error_message = "\nUnknown option:";

        while (@ARGV) {
            $error_message .= q{ };
            $error_message .= shift;
        }
        $error_message .= "\n";

        $cl_options{help} = 1;
    }
}
else {

    $cl_options{help} = 0;
}

#####################################
# Test function or display variables
# or anything else I need.

if ( $cl_options{test} ) {

    print "No tests set\n";
    exit;
}

#####################################
# Warning Level

if ( exists $numeric_warning_level_for{ $cl_options{warning_level} } ) {
    # We convert the warning level from
    # a string to a numerical value
    $cl_options{warning_level} = $numeric_warning_level_for{ $cl_options{warning_level} };
}
else {
        $error_message .= "\nInvalid warning level: $cl_options{warning_level}\n"
                       .  "Valid options are: error, warning, notice, info and debug\n";
        $cl_options{help} = 1;
}


#####################################
# No-warning option

if ( $cl_options{nowarning} && $cl_options{warning_level} >= INFO ) {
    $cl_options{warning_level} = NOTICE;
}

#####################################
# Convertion options

if ( $cl_options{convert} ) {
    if ( $cl_options{convert} eq 'Willpower' ) {
#        $conversion_enable{'ALL:PRERACE needs a ,'} = 1;
        $conversion_enable{'ALL:Willpower to Will'} = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen595' ) {
#        $conversion_enable{'ALL:PRERACE needs a ,'} = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen580' ) {
        $conversion_enable{'ALL:PREALING conversion'} = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen5713' ) {
        $conversion_enable{'ALL:Convert SPELL to SPELLS'}    = 1;
        $conversion_enable{'TEMPLATE:HITDICESIZE to HITDIE'} = 1;
        $conversion_enable{'ALL:PRECLASS needs a ,'}         = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen574' ) {
        $conversion_enable{'CLASS:CASTERLEVEL for all casters'} = 1;
        $conversion_enable{'ALL:MOVE:nn to MOVE:Walk,nn'}       = 1;
    }

    #  elsif($cl_options{convert} eq 'pcgen56')
    #  {
    #    $conversion_enable{'ALL:EQMOD has new keys'} = 1;
    #  }
    elsif ( $cl_options{convert} eq 'pcgen555' ) {
        $conversion_enable{'EQUIP:no more MOVE'} = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen541' ) {
        $conversion_enable{'WEAPONPROF:No more SIZE'} = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen54' ) {
        $conversion_enable{'PCC:GAMEMODE DnD to 3e'}    = 1;
        $conversion_enable{'PCC:GAME to GAMEMODE'}      = 1;
        $conversion_enable{'ALL:Add TYPE=Base.REPLACE'} = 1;
        $conversion_enable{'RACE:CSKILL to MONCSKILL'}  = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen54cmp' ) {
        $conversion_enable{'PCC:GAME to GAMEMODE'}      = 1;
        $conversion_enable{'ALL:Add TYPE=Base.REPLACE'} = 1;
        $conversion_enable{'RACE:CSKILL to MONCSKILL'}  = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen534' ) {

        #    $conversion_enable{'ALL:COUNT[FEATTYPE=...'} = 1;
        $conversion_enable{'PCC:GAME to GAMEMODE'}      = 1;
        $conversion_enable{'ALL:Add TYPE=Base.REPLACE'} = 1;

        #    $conversion_enable{'CLASS: SPELLLIST from Spell.MOD'} = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen511' ) {
        $conversion_enable{'ALL: , to | in VISION'}  = 1;
        $conversion_enable{'ALL:PRECLASS needs a ,'} = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen438' ) {
        $conversion_enable{'ALL:PRESTAT needs a ,'}          = 1;
        $conversion_enable{'EQUIPMENT: remove ATTACKS'}      = 1;
        $conversion_enable{'EQUIPMENT: SLOTS:2 for plurals'} = 1;
    }
    elsif ( $cl_options{convert} eq 'pcgen433' ) {
        $conversion_enable{'ALL: 4.3.3 Weapon name change'} = 1;
    }
    elsif ( $cl_options{convert} eq 'rmprealign' ) {
        $conversion_enable{'ALL:CMP remove PREALIGN'} = 1;
    }
    elsif ( $cl_options{convert} eq 'natattackfix' ) {
        $conversion_enable{'ALL:CMP NatAttack fix'} = 1;
    }
    elsif ( $cl_options{convert} eq 'skillbonusfix' ) {
        $conversion_enable{'RACE:BONUS SKILL Climb and Swim'} = 1;
    }
    elsif ( $cl_options{convert} eq 'noprofreq' ) {

        # [ 832164 ] Adding NoProfReq to AUTO:WEAPONPROF for most races
        $conversion_enable{'RACE:NoProfReq'} = 1;
    }
    elsif ( $cl_options{convert} eq 'classspell' ) {
        $conversion_enable{'CLASSSPELL convertion to SPELL'} = 1;
    }
    elsif ( $cl_options{convert} eq 'classskill' ) {
        $conversion_enable{'CLASSSKILL convertion to CLASS'} = 1;
    }
    elsif ( $cl_options{convert} eq 'notready' ) {
        $conversion_enable{'ALL:BONUS:MOVE convertion'} = 1;
    }
    elsif ($cl_options{convert} eq 'foldbacklines'
        || $cl_options{convert} eq 'ml21' )
    {
        $conversion_enable{'ALL:Multiple lines to one'} = 1;
    }
    elsif ( $cl_options{convert} eq 'gmconv' ) {
        $conversion_enable{'PCC:GAMEMODE Add to the CMP DnD_'} = 1;
    }
    else {
        $error_message .= "\nUnknown convertion option: $cl_options{convert}\n";
        $cl_options{help} = 1;
    }

}

#####################################
# old_source_tag option

if ( $cl_options{old_source_tag} ) {
    # We disable the convertion if the -old_source_tag option is used
    $conversion_enable{'ALL:New SOURCExxx tag format'} = 0;
}

#####################################
# exportlist option

if ( $cl_options{exportlist} ) {
    $conversion_enable{'Export lists'} = 1;
}

#####################################
# noxcheck option

if ( $cl_options{noxcheck} ) {

    # The xcheck option is now on by default.
    # using noxcheck is the only way to disable it
    $cl_options{xcheck} = 0;
}

#####################################
# Path options

if (   !$cl_options{input_path}
    && !$cl_options{file_type}
    && !( $cl_options{man} || $cl_options{html_help} ) )
{
    $error_message .= "\n-inputpath parameter is missing\n";
    $cl_options{help} = 1;
}

#####################################
# -basepath option
#
# If no basepath were given, use
# input_dir

if ( $cl_options{basepath} eq q{} ) {
    $cl_options{basepath} = $cl_options{input_path};
}

$cl_options{basepath} =~ tr{\\}{/};

#####################################
# Redirect STDERR if needed

if ($cl_options{output_error}) {
    open STDERR, '>', $cl_options{output_error};
}

# List of default for values defined in system files
my @valid_system_alignments     = qw( LG  LN  LE  NG  TN  NE  CG  CN  CE  NONE  Deity );

my @valid_system_check_names = qw( Fortitude Reflex Will );

my @valid_system_game_modes     = qw(
    35e 3e Deadlands DnD LoE Modern Spycraft Xcrawl

    CMP_D20_Fantasy_v30e
    CMP_D20_Fantasy_v35e
    CMP_D20_Modern
    CMP_DnD_Dragonlance
    CMP_DnD_Eberron
    CMP_DnD_forgotten_realms_v30e
    CMP_DnD_forgotten_realms_v35e
    CMP_DnD_oriental_adventures_v30e
    CMP_DnD_oriental_adventures_v35e
    CMP_HARP
);

my @valid_system_stats          = qw(
    STR DEX CON INT WIS CHA NOB FAM PFM

    DVR WEA AGI QUI SDI REA INS PRE
);

my @valid_system_var_names      = qw(
    ACTIONDICE              ACTIONDIEBONUS              ACTIONDIETYPE
    Action                  ActionLVL                   BUDGETPOINTS
    CURRENTVEHICLEMODS      ClassDefense                DamageThreshold
    EDUCATION               EDUCATIONMISC               FAVORCHECK
    FIGHTINGDEFENSIVELYAC   FightingDefensivelyAC       FightingDefensivelyACBonus
    GADGETPOINTS            INITCOMP                    INSPIRATION
    INSPIRATIONMISC         LOADSCORE                   MAXLEVELSTAT
    MAXVEHICLEMODS          MISSIONBUDGET               MUSCLE
    MXDXEN                  NATIVELANGUAGES             NORMALMOUNT
    OFFHANDLIGHTBONUS       PSIONLEVEL                  Reputation
    TWOHANDDAMAGEDIVISOR    TotalDefenseAC              TotalDefenseACBonus
    UseAlternateDamage      VEHICLECRUISINGMPH          VEHICLEDEFENSE
    VEHICLEHANDLING         VEHICLEHARDNESS             VEHICLESPEED
    VEHICLETOPMPH           VEHICLEWOUNDPOINTS          Wealth

    Action                      ActionLVL                   ArmorQui
    ClassDefense                DamageThreshold             DenseMuscle
    FIGHTINGDEFENSIVELYACBONUS  Giantism                    INITCOMP
    LOADSCORE                   MAXLEVELSTAT                MUSCLE
    MXDXEN                      Mount                       OFFHANDLIGHTBONUS
    TOTALDEFENSEACBONUS         TWOHANDDAMAGEDIVISOR
);

#####################################
# -systempath option
#
# If present, call the function to
# generate the "game mode" variables.

if ( $cl_options{system_path} ne q{} ) {
    parse_system_files($cl_options{system_path});
}

# Valid check name
my %valid_check_name = map { $_ => 1} @valid_system_check_names;

# Valid game type (for the .PCC files)
my %valid_game_modes = map { $_ => 1 } (
    @valid_system_game_modes,

    # CMP game modes
    'CMP_OGL_Arcana_Unearthed',
    'CMP_DnD_Dragonlance',
    'CMP_DnD_Eberron',
    'CMP_DnD_forgotten_realms_v30e',
    'CMP_DnD_forgotten_realms_v35e',
    'CMP_HARP',
    'CMP_D20_Modern',
    'CMP_DnD_oriental_adventures_v30e',
    'CMP_DnD_oriental_adventures_v35e',
    'CMP_D20_Fantasy_v30e',,
    'CMP_D20_Fantasy_v35e',,
    'DnD_v3.5e_VPWP',,
    'CMP_D20_Fantasy_v35e_VPWP',

);

# Limited choice tags
my %tag_fix_value = (
    ACHECK         => { YES => 1, NO => 1, WEIGHT => 1, PROFICIENT => 1, DOUBLE => 1 },
    ALIGN          => { map { $_ => 1 } @valid_system_alignments },
    BONUSSPELLSTAT => { map { $_ => 1 } ( @valid_system_stats, 'NONE' ) },
    DESCISIP       => { YES => 1, NO => 1 },
    EXCLUSIVE      => { YES => 1, NO => 1 },
    FREE           => { YES => 1, NO => 1 },
    KEYSTAT        => { map { $_ => 1 } @valid_system_stats },
    HASSUBCLASS    => { YES => 1, NO => 1 },
    ISD20          => { YES => 1, NO => 1 },
    ISLICENCED     => { YES => 1, NO => 1 },
    ISOGL          => { YES => 1, NO => 1 },
    MEMORIZE       => { YES => 1, NO => 1 },
    MULT           => { YES => 1, NO => 1 },
    MODS           => { YES => 1, NO => 1, REQUIRED => 1 },
    MODTOSKILLS    => { YES => 1, NO => 1 },
    NAMEISPI       => { YES => 1, NO => 1 },
    RACIAL         => { YES => 1, NO => 1 },
    REMOVABLE      => { YES => 1, NO => 1 },
    PREALIGN       => { map { $_ => 1 } @valid_system_alignments },
    PRESPELLBOOK   => { YES => 1, NO => 1 },
    STACK          => { YES => 1, NO => 1 },
    SPELLBOOK      => { YES => 1, NO => 1 },
    SPELLSTAT      => { map { $_ => 1 } ( @valid_system_stats, 'SPELL' ) },
    USEUNTRAINED   => { YES => 1, NO => 1 },
    USEMASTERSKILL => { YES => 1, NO => 1 },
    VISIBLE        => { YES => 1, NO => 1, EXPORT => 1, DISPLAY => 1, QUALIFY => 1 },
);

# This hash is used to convert 1 character choices to proper fix values.
my %tag_proper_value_for = (
    'Y'    =>   'YES',
    'N'    =>   'NO',
    'W'    =>   'WEIGHT',
    'Q'    =>   'QUALIFY',
    'P'    =>   'PROFICIENT',
    'R'    =>   'REQUIRED',

);

#####################################
# Diplay usage information

if ( $cl_options{help} or $Getopt::Long::error ) {
    Pod::Usage::pod2usage(
        {   -msg     => $error_message,
            -exitval => 1,
            -output  => \*STDERR
        }
    );
    exit;
}

#####################################
# Display the man page

if ($cl_options{man}) {
    Pod::Usage::pod2usage(
        {   -msg     => $error_message,
            -verbose => 2,
            -output  => \*STDERR
        }
    );
    exit;
}

#####################################
# Generate the HTML man page and display it

if ( $cl_options{html_help} ) {
    if( !-e "$PROGRAM_NAME.css" ) {
        generate_css("$PROGRAM_NAME.css");
    }

    Pod::Html::pod2html(
        "--infile=$PROGRAM_NAME",
        "--outfile=$PROGRAM_NAME.html",
        "--css=$PROGRAM_NAME.css",
        "--title=$PROGRAM_NAME -- Reformat the PCGEN .lst files",
        '--header',
    );

    `start /max $PROGRAM_NAME.html`;

    exit;
}

my %source_tags             = ()    if $conversion_enable{'SOURCE line replacement'};
my $source_curent_file      = q{}   if $conversion_enable{'SOURCE line replacement'};

my %classskill_files        = ()    if $conversion_enable{'CLASSSKILL convertion to CLASS'};

my %classspell_files        = ()    if $conversion_enable{'CLASSSPELL convertion to SPELL'};

my %class_files             = ()    if $conversion_enable{'SPELL:Add TYPE tags'};
my %class_spelltypes        = ()    if $conversion_enable{'SPELL:Add TYPE tags'};

my %Spells_For_EQMOD        = ()    if $conversion_enable{'EQUIPMENT: generate EQMOD'};
my %Spell_Files             = ()    if $conversion_enable{'EQUIPMENT: generate EQMOD'}
                                       || $conversion_enable{'CLASS: SPELLLIST from Spell.MOD'};

my %bonus_prexxx_tag_report = ()    if $conversion_enable{'Generate BONUS and PRExxx report'};

my %PREALIGN_conversion_5715 = qw(
    0     LG
    1     LN
    2     LE
    3     NG
    4     TN
    5     NE
    6     CG
    7     CN
    8     CE
    9     NONE
    10    Deity
) if $conversion_enable{'ALL:PREALING conversion'};

#my %Key_conversion_56 = qw(
#  ABENHABON     BNS_ENHC_AB
#  ABILITYMINUS    BNS_ENHC_AB
#  ABILITYPLUS     BNS_ENHC_AB
#  ACDEFLBON     BNS_AC_DEFL
#  ACENHABON     BNS_ENHC_AC
#  ACINSIBON     BNS_AC_INSI
#  ACLUCKBON     BNS_AC_LUCK
#  ACOTHEBON     BNS_AC_OTHE
#  ACPROFBON     BNS_AC_PROF
#  ACSACRBON     BNS_AC_SCRD
#  ADAARH      ADAM
#  ADAARH      ADAM
#  ADAARL      ADAM
#  ADAARM      ADAM
#  ADAWE       ADAM
#  AMINAT      ANMATD
#  AMMO+1      PLUS1W
#  AMMO+2      PLUS2W
#  AMMO+3      PLUS3W
#  AMMO+4      PLUS4W
#  AMMO+5      PLUS5W
#  AMMODARK      DARK
#  AMMOSLVR      SLVR
#  ARFORH      FRT_HVY
#  ARFORL      FRT_LGHT
#  ARFORM      FRT_MOD
#  ARMFOR      FRT_LGHT
#  ARMFORH     FRT_HVY
#  ARMFORM     FRT_MOD
#  ARMORENHANCE    BNS_ENHC_AC
#  ARMR+1      PLUS1A
#  ARMR+2      PLUS2A
#  ARMR+3      PLUS3A
#  ARMR+4      PLUS4A
#  ARMR+5      PLUS5A
#  ARMRADMH      ADAM
#  ARMRADML      ADAM
#  ARMRADMM      ADAM
#  ARMRMITH      MTHRL
#  ARMRMITL      MTHRL
#  ARMRMITM      MTHRL
#  ARWCAT      ARW_CAT
#  ARWDEF      ARW_DEF
#  BANEA       BANE_A
#  BANEM       BANE_M
#  BANER       BANE_R
#  BASHH       BASH_H
#  BASHL       BASH_L
#  BIND        BLIND
#  BONSPELL      BNS_SPELL
#  BONUSSPELL      BNS_SPELL
#  BRIENAI     BRI_EN_A
#  BRIENM      BRI_EN_M
#  BRIENT      BRI_EN_T
#  CHAOSA      CHAOS_A
#  CHAOSM      CHAOS_M
#  CHAOSR      CHAOS_R
#  CLDIRNAI      CIRON
#  CLDIRNW     CIRON
#  DAGSLVR     SLVR
#  DEFLECTBONUS    BNS_AC_DEFL
#  DRGNAR      DRACO
#  DRGNSH      DRACO
#  DRKAMI      DARK
#  DRKSH       DARK
#  DRKWE       DARK
#  ENBURM      EN_BUR_M
#  ENBURR      EN_BUR_R
#  ENERGM      ENERG_M
#  ENERGR      ENERG_R
#  FLAMA       FLM_A
#  FLAMM       FLM_M
#  FLAMR       FLM_R
#  FLBURA      FLM_BR_A
#  FLBURM      FLM_BR_M
#  FLBURR      FLM_BR_R
#  FROSA       FROST_A
#  FROSM       FROST_M
#  FROSR       FROST_R
#  GHTOUA      GHOST_A
#  GHTOUAM     GHOST_AM
#  GHTOUM      GHOST_M
#  GHTOUR      GHOST_R
#  HCLDIRNW      CIRON/2
#  HOLYA       HOLY_A
#  HOLYM       HOLY_M
#  HOLYR       HOLY_R
#  ICBURA      ICE_BR_A
#  ICBURM      ICE_BR_M
#  ICBURR      ICE_BR_R
#  LAWA        LAW_A
#  LAWM        LAW_M
#  LAWR        LAW_R
#  LUCKBONUS     BNS_SAV_LUC
#  LUCKBONUS2      BNS_SKL_LCK
#  MERCA       MERC_A
#  MERCM       MERC_M
#  MERCR       MERC_R
#  MICLE       MI_CLE
#  MITHAMI     MTHRL
#  MITHARH     MTHRL
#  MITHARL     MTHRL
#  MITHARM     MTHRL
#  MITHGO      MTHRL
#  MITHSH      MTHRL
#  MITHWE      MTHRL
#  NATENHA     BNS_ENHC_NAT
#  NATURALARMOR    BNS_ENHC_NAT
#  PLUS1AM     PLUS1W
#  PLUS1AMI      PLUS1W
#  PLUS1WI     PLUS1W
#  PLUS2AM     PLUS2W
#  PLUS2AMI      PLUS2W
#  PLUS2WI     PLUS2W
#  PLUS3AM     PLUS3W
#  PLUS3AMI      PLUS3W
#  PLUS3WI     PLUS3W
#  PLUS4AM     PLUS4W
#  PLUS4AMI      PLUS4W
#  PLUS4WI     PLUS4W
#  PLUS5AM     PLUS5W
#  PLUS5AMI      PLUS5W
#  PLUS5WI     PLUS5W
#  RESIMP      RST_IMP
#  RESIST      RST_IST
#  RESISTBONUS     BNS_SAV_RES
#  SAVINSBON     BNS_SAV_INS
#  SAVLUCBON     BNS_SAV_LUC
#  SAVOTHBON     BNS_SAV_OTH
#  SAVPROBON     BNS_SAV_PRO
#  SAVRESBON     BNS_SAV_RES
#  SAVSACBON     BNS_SAV_SAC
#  SE50CST     SPL_CHRG
#  SECW        SPL_CMD
#  SESUCAMA      A_1USEMI
#  SESUCAME      A_1USEMI
#  SESUCAMI      A_1USEMI
#  SESUCDMA      D_1USEMI
#  SESUCDME      D_1USEMI
#  SESUCDMI      D_1USEMI
#  SESUUA      SPL_1USE
#  SEUA        SPL_ACT
#  SE_1USEACT      SPL_1USE
#  SE_50TRIGGER    SPL_CHRG
#  SE_COMMANDWORD    SPL_CMD
#  SE_USEACT     SPL_ACT
#  SHBURA      SHK_BR_A
#  SHBURM      SHK_BR_M
#  SHBURR      SHK_BR_R
#  SHDGRT      SHDW_GRT
#  SHDIMP      SHDW_IMP
#  SHDOW       SHDW
#  SHFORH      FRT_HVY
#  SHFORL      FRT_LGHT
#  SHFORM      FRT_MOD
#  SHLDADAM      ADAM
#  SHLDDARK      DARK
#  SHLDMITH      MTHRL
#  SHOCA       SHOCK_A
#  SHOCM       SHOCK_M
#  SHOCR       SHOCK_R
#  SKILLBONUS      BNS_SKL_CIR
#  SKILLBONUS2     BNS_SKL_CMP
#  SKLCOMBON     BNS_SKL_CMP
#  SLICK       SLK
#  SLKGRT      SLK_GRT
#  SLKIMP      SLK_IMP
#  SLMV        SLNT_MV
#  SLMVGRT     SLNT_MV_GRT
#  SLMVIM      SLNT_MV_IM
#  SLVRAMI     ALCHM
#  SLVRWE1     ALCHM
#  SLVRWE2     ALCHM
#  SLVRWEF     ALCHM
#  SLVRWEH     ALCHM/2
#  SLVRWEL     ALCHM
#  SPELLRESI     BNS_SPL_RST
#  SPELLRESIST     BNS_SPL_RST
#  SPLRES      SPL_RST
#  SPLSTR      SPL_STR
#  THNDRA      THNDR_A
#  THNDRM      THNDR_M
#  THNDRR      THNDR_R
#  UNHLYA      UNHLY_A
#  UNHLYM      UNHLY_M
#  UNHLYR      UNHLY_R
#  WEAP+1      PLUS1W
#  WEAP+2      PLUS2W
#  WEAP+3      PLUS3W
#  WEAP+4      PLUS4W
#  WEAP+5      PLUS5W
#  WEAPADAM      ADAM
#  WEAPDARK      DARK
#  WEAPMITH      MTHRL
#  WILDA       WILD_A
#  WILDS       WILD_S
#   ) if $conversion_enable{'ALL:EQMOD has new keys'};

#if($conversion_enable{'ALL:EQMOD has new keys'})
#{
#  my ($old_key,$new_key);
#  while (($old_key,$new_key) = each %Key_conversion_56)
#  {
#    if($old_key eq $new_key)
#    {
#      print "==> $old_key\n";
#      delete $Key_conversion_56{$old_key};
#    }
#  }
#}

my %srd_weapon_name_convertion_433 = (
    q{Sword (Great)}               => q{Greatsword},
    q{Sword (Long)}                => q{Longsword},
    q{Dagger (Venom)}              => q{Venom Dagger},
    q{Dagger (Assassin's)}         => q{Assassin's Dagger},
    q{Mace (Smiting)}              => q{Mace of Smiting},
    q{Mace (Terror)}               => q{Mace of Terror},
    q{Greataxe (Life-Drinker)}     => q{Life Drinker},
    q{Rapier (Puncturing)}         => q{Rapier of Puncturing},
    q{Scimitar (Sylvan)}           => q{Sylvan Scimitar},
    q{Sword (Flame Tongue)}        => q{Flame Tongue},
    q{Sword (Planes)}              => q{Sword of the Planes},
    q{Sword (Luck Blade)}          => q{Luck Blade},
    q{Sword (Subtlety)}            => q{Sword of Subtlety},
    q{Sword (Holy Avenger)}        => q{Holy Avenger},
    q{Sword (Life Stealing)}       => q{Sword of Life Stealing},
    q{Sword (Nine Lives Stealer)}  => q{Nine Lives Stealer},
    q{Sword (Frost Brand)}         => q{Frost Brand},
    q{Trident (Fish Command)}      => q{Trident of Fish Command},
    q{Trident (Warning)}           => q{Trident of Warning},
    q{Warhammer (Dwarven Thrower)} => q{Dwarven Thrower},
) if $conversion_enable{'ALL: 4.3.3 Weapon name change'};


# Constants for master_line_type

# Line importance (Mode)
use constant MAIN           => 1;   # Main line type for the file
use constant SUB            => 2;   # Sub line type, must be linked to a MAIN
use constant SINGLE         => 3;   # Idependant line type
use constant COMMENT        => 4;   # Comment or empty line.

# Line formatting option
use constant LINE           => 1;   # Every line formatted by itself
use constant BLOCK          => 2;   # Lines formatted as a block
use constant FIRST_COLUMN   => 3;   # Only the first column of the block
                                    # gets aligned

# Line header option
use constant NO_HEADER      => 1;   # No header
use constant LINE_HEADER    => 2;   # One header before each line
use constant BLOCK_HEADER   => 3;   # One header for the block

# Standard YES NO constants
use constant NO  => 0;
use constant YES => 1;

# The SOURCE line is use in nearly all file type
my %SOURCE_file_type_def = (
            Linetype        => 'SOURCE',
            RegEx           => qr(^SOURCE\w*:([^\t]*)),
            Mode            => SINGLE,
            Format          => LINE,
            Header          => NO_HEADER,
#            Sep             => q{|},                          # use | instead of [tab] to split
            SepRegEx        => qr{ (?: [|] ) | (?: \t+ ) }xms,  # Catch both | and tab
);

# Some ppl may still want to use the old ways (for PCGen v5.9.5 and older)
if( $cl_options{old_source_tag} ) {
    $SOURCE_file_type_def{Sep} = q{|};  # use | instead of [tab] to split
}

# Information needed to parse the line type
my %master_file_type = (

    BIOSET => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'BIOSET AGESET',
            RegEx           => qr(^AGESET:([^\t]*)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => NO_HEADER,
            ValidateKeep    => YES,
            RegExIsMod      => qr(AGESET:(.*)\.([^\t]+)),
            RegExGetEntry   => qr(AGESET:(.*)),
        },
        {   Linetype        => 'BIOSET RACENAME',
            RegEx           => qr(^RACENAME:([^\t]*)),
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
    ],

    CLASS => [
        {   Linetype        => 'CLASS Level',
            RegEx           => qr(^(\d+)($|\t)),
            Mode            => SUB,
            Format          => BLOCK,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'CLASS',
            RegEx           => qr(^CLASS:([^\t]*)),
            Mode            => MAIN,
            Format          => LINE,
            Header          => LINE_HEADER,
            ValidateKeep    => YES,
            RegExIsMod      => qr(CLASS:(.*)\.(MOD|FORGET|COPY=[^\t]+)),
            RegExGetEntry   => qr(CLASS:(.*)),
        },
        \%SOURCE_file_type_def,
        {   Linetype        => 'SUBCLASS',
            RegEx           => qr(^SUBCLASS:([^\t]*)),
            Mode            => SUB,
            Format          => BLOCK,
            Header          => NO_HEADER,
            ValidateKeep    => YES,
            RegExIsMod      => qr(SUBCLASS:(.*)\.(MOD|FORGET|COPY=[^\t]+)),
            RegExGetEntry   => qr(SUBCLASS:(.*)),

            # SUBCLASS can be refered to anywhere CLASS works.
            OtherValidEntries => ['CLASS'],
        },
        {   Linetype        => 'SUBCLASSLEVEL',
            RegEx           => qr(^SUBCLASSLEVEL:([^\t]*)),
            Mode            => SUB,
            Format          => BLOCK,
            Header          => NO_HEADER,
        },
    ],

    COMPANIONMOD => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'SWITCHRACE',
            RegEx           => qr(^SWITCHRACE:([^\t]*)),
            Mode            => SINGLE,
            Format          => LINE,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'COMPANIONMOD',
            RegEx           => qr(^FOLLOWER:([^\t]*)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
            RegExIsMod      => qr(FOLLOWER:(.*)\.(MOD|FORGET|COPY=[^\t]+)),
            RegExGetEntry   => qr(FOLLOWER:(.*)),

            # Identifier that refer to other entry type
            IdentRefType    => 'CLASS,DEFINE Variable',
            IdentRefTag     => 'FOLLOWER',                # Tag name for the reference check
                                                          # Get the list of reference identifiers
                                                          # The syntax is FOLLOWER:class1,class2=level
                                                          # We need to extract the class names.
            GetRefList => sub { split q{,}, ( $_[0] =~ / \A ( [^=]* ) /xms )[0]  },
        },
        {   Linetype        => 'MASTERBONUSRACE',
            RegEx           => qr(^MASTERBONUSRACE:([^\t]*)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
            RegExIsMod      => qr(MASTERBONUSRACE:(.*)\.(MOD|FORGET|COPY=[^\t]+)),
            RegExGetEntry   => qr(MASTERBONUSRACE:(.*)),
            IdentRefType    => 'RACE',                    # Identifier that refer to other entry type
            IdentRefTag     => 'MASTERBONUSRACE',         # Tag name for the reference check
                                                          # Get the list of reference identifiers
                                                          # The syntax is MASTERBONUSRACE:race
                                                          # We need to extract the race name.
            GetRefList      => sub { return @_ },
        },
    ],

    DEITY => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'DEITY',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    DOMAIN => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'DOMAIN',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    EQUIPMENT => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'EQUIPMENT',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    EQUIPMOD => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'EQUIPMOD',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    FEAT => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'FEAT',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    KIT => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'KIT REGION',
            RegEx           => qr{^REGION:([^\t]*)},
            Mode            => SINGLE,
            Format          => LINE,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT STARTPACK',         # The KIT name is defined here
            RegEx           => qr{^STARTPACK:([^\t]*)},
            Mode            => MAIN,
            Format          => LINE,
            Header          => NO_HEADER,
            ValidateKeep    => YES,
        },
        {   Linetype        => 'KIT ALIGN',
            RegEx           => qr{^ALIGN:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT CLASS',
            RegEx           => qr{^CLASS:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT DEITY',
            RegEx           => qr{^DEITY:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT FEAT',
            RegEx           => qr{^FEAT:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT GEAR',
            RegEx           => qr{^GEAR:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT KIT',
            RegEx           => qr{^KIT:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT LANGAUTO',
            RegEx           => qr{^LANGAUTO:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT NAME',
            RegEx           => qr{^NAME:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT PROF',
            RegEx           => qr{^PROF:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT RACE',
            RegEx           => qr{^RACE:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT SKILL',
            RegEx           => qr{^SKILL:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT STAT',
            RegEx           => qr{^STAT:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT SPELLS',
            RegEx           => qr{^SPELLS:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
        {   Linetype        => 'KIT TEMPLATE',
            RegEx           => qr{^TEMPLATE:([^\t]*)},
            Mode            => SUB,
            Format          => FIRST_COLUMN,
            Header          => NO_HEADER,
        },
    ],

    LANGUAGE => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'LANGUAGE',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    RACE => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'RACE',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    SKILL => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'SKILL',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    SPELL => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'SPELL',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    TEMPLATE => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'TEMPLATE',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],

    WEAPONPROF => [
        \%SOURCE_file_type_def,
        {   Linetype        => 'WEAPONPROF',
            RegEx           => qr(^([^\t:]+)),
            Mode            => MAIN,
            Format          => BLOCK,
            Header          => BLOCK_HEADER,
            ValidateKeep    => YES,
        },
    ],
);

# The PRExxx tags. They are used in many of the line types.
# From now on, they are defined in only one place and every
# line type will get the same sort order.
my @PRE_Tags = (
    'PRE:.CLEAR',
    'PREALIGN',
    '!PREALIGN',
    'PREARMORPROF:*',
    '!PREARMORPROF',
    'PREARMORTYPE',
    '!PREARMORTYPE',
    'PREATT',
    '!PREATT',
    'PREBASESIZEEQ',
    '!PREBASESIZEEQ',
    'PREBASESIZEGT',
    '!PREBASESIZEGT',
    'PREBASESIZEGTEQ',
    '!PREBASESIZEGTEQ',
    'PREBASESIZELT',
    '!PREBASESIZELT',
    'PREBASESIZELTEQ',
    '!PREBASESIZELTEQ',
    'PREBASESIZENEQ',
    'PREBIRTHPLACE',
    '!PREBIRTHPLACE',
    'PRECHECK',
    '!PRECHECK',
    'PRECHECKBASE',
    '!PRECHECKBASE',
    'PRECITY',
    '!PRECITY',
    'PRECLASS',
    '!PRECLASS',
    'PRECLASSLEVELMAX',
    '!PRECLASSLEVELMAX',
    'PRECSKILL',
    '!PRECSKILL',
    'PREDEITY',
    '!PREDEITY',
    'PREDEITYALIGN',
    '!PREDEITYALIGN',
    'PREDEITYDOMAIN',
    '!PREDEITYDOMAIN',
    'PREDOMAIN',
    '!PREDOMAIN',
    'PREDR',
    '!PREDR',
    'PREEQUIP',
    '!PREEQUIP',
    'PREEQUIPBOTH',
    '!PREEQUIPBOTH',
    'PREEQUIPPRIMARY',
    '!PREEQUIPPRIMARY',
    'PREEQUIPSECONDARY',
    '!PREEQUIPSECONDARY',
    'PREEQUIPTWOWEAPON',
    '!PREEQUIPTWOWEAPON',
    'PREFEAT:*',
    '!PREFEAT',
    'PREGENDER',
    '!PREGENDER',
    'PREHANDSEQ',
    '!PREHANDSEQ',
    'PREHANDSGT',
    '!PREHANDSGT',
    'PREHANDSGTEQ',
    '!PREHANDSGTEQ',
    'PREHANDSLT',
    '!PREHANDSLT',
    'PREHANDSLTEQ',
    '!PREHANDSLTEQ',
    'PREHANDSNEQ',
    'PREHD',
    '!PREHD',
    'PREHP',
    '!PREHP',
    'PREITEM',
    '!PREITEM',
    'PRELANG',
    '!PRELANG',
    'PRELEGSEQ',
    '!PRELEGSEQ',
    'PRELEGSGT',
    '!PRELEGSGT',
    'PRELEGSGTEQ',
    '!PRELEGSGTEQ',
    'PRELEGSLT',
    '!PRELEGSLT',
    'PRELEGSLTEQ',
    '!PRELEGSLTEQ',
    'PRELEGSNEQ',
    'PRELEVEL',
    '!PRELEVEL',
    'PRELEVELMAX',
    '!PRELEVELMAX',
    'PREMOVE',
    '!PREMOVE',
    'PREMULT',
    '!PREMULT',
    'PRERACE',
    '!PRERACE',
    'PREREGION',
    '!PREREGION',
    'PRERULE',
    'PRESA',
    '!PRESA',
    'PRESHIELDPROF',
    '!PRESHIELDPROF',
    'PRESIZEEQ',
    '!PRESIZEEQ',
    'PRESIZEGT',
    '!PRESIZEGT',
    'PRESIZEGTEQ',
    '!PRESIZEGTEQ',
    'PRESIZELT',
    '!PRESIZELT',
    'PRESIZELTEQ',
    '!PRESIZELTEQ',
    'PRESIZENEQ',
    'PRESKILL:*',
    '!PRESKILL',
    'PRESKILLMULT',
    '!PRESKILLMULT',
    'PRESKILLTOT',
    '!PRESKILLTOT',
    'PRESPELL:*',
    '!PRESPELL',
    'PRESPELLBOOK',
    '!PRESPELLBOOK',
    'PRESPELLCAST:*',
    '!PRESPELLCAST:*',
    'PRESPELLDESCRIPTOR',
    'PRESPELLSCHOOL:*',
    '!PRESPELLSCHOOL',
    'PRESPELLSCHOOLSUB',
    '!PRESPELLSCHOOLSUB',
    'PRESPELLTYPE:*',
    '!PRESPELLTYPE',
    'PRESREQ',
    '!PRESREQ',
    'PRESRGT',
    '!PRESRGT',
    'PRESRGTEQ',
    '!PRESRGTEQ',
    'PRESRLT',
    '!PRESRLT',
    'PRESRLTEQ',
    '!PRESRLTEQ',
    'PRESRNEQ',
    'PRESTAT:*',
    '!PRESTAT',
    'PRESTATEQ',
    '!PRESTATEQ',
    'PRESTATGT',
    '!PRESTATGT',
    'PRESTATGTEQ',
    '!PRESTATGTEQ',
    'PRESTATLT',
    '!PRESTATLT',
    'PRESTATLTEQ',
    '!PRESTATLTEQ',
    'PRESTATNEQ',
    'PRESUBCLASS',
    '!PRESUBCLASS',
    'PRETEMPLATE:*',
    '!PRETEMPLATE:*',
    'PRETEXT',
    '!PRETEXT',
    'PRETYPE:*',
    '!PRETYPE:*',
    'PREUATT',
    '!PREUATT',
    'PREVAREQ:*',
    '!PREVAREQ:*',
    'PREVARGT:*',
    '!PREVARGT:*',
    'PREVARGTEQ:*',
    '!PREVARGTEQ:*',
    'PREVARLT:*',
    '!PREVARLT:*',
    'PREVARLTEQ:*',
    '!PREVARLTEQ:*',
    'PREVARNEQ:*',
    'PREVISION',
    '!PREVISION',
    'PREWEAPONPROF',
    '!PREWEAPONPROF',
    'PREWIELD',
    '!PREWIELD',

    # Removed tags
    #    'PREVAR',
);

# Hash used by validate_pre_tag to verify if a PRExxx tag exists
my %PRE_Tags = (
    'PREAPPLY'          => 1,    # Only valid when embeded
    'PREDEFAULTMONSTER' => 1,    # Only valid when embeded
);

for my $pre_tag (@PRE_Tags) {
    # We need a copy since we don't want to modify the original
    my $pre_tag_name = $pre_tag;

    # We strip the :* at the end to get the real name for the lookup table
    $pre_tag_name =~ s/ [:][*] \z//xms;

    $PRE_Tags{$pre_tag_name} = 1;
}


# Order for the tags for each line type.
my %master_order = (
    'BIOSET AGESET' => [
        'AGESET',
        'BONUS:STAT:*',
    ],

    'BIOSET RACENAME' => [
        'RACENAME',
        'CLASS',
        'SEX',
        'BASEAGE',
        'MAXAGE',
        'AGEDIEROLL',
        'HAIR',
        'EYES',
        'SKINTONE',

    ],

    'CLASS' => [
        '000ClassName',
        'NAMEISPI',
        'OUTPUTNAME',
        'HD',
        'XTRAFEATS',
        'SPELLSTAT',
        'BONUSSPELLSTAT',
        'BONUS:DC',
        'SPELLTYPE',
        'TYPE',
        'ABB',
        'MAXLEVEL',
        'CASTAS',
        'MEMORIZE',
        'KNOWNSPELLS',
        'SPELLBOOK',
        'HASSUBCLASS',
        'EXCLASS',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE:.CLEAR',
        'SOURCEPAGE',
        'LANGAUTO',
        'LANGBONUS',
        'WEAPONBONUS',
        'VISION',
        'SR',
        'DR',
        'ATTACKCYCLE',
        'DEF',
        'ITEMCREATE',
        'KNOWNSPELLSFROMSPECIALTY',
        'PROHIBITED',
        'LEVELSPERFEAT',
        'VFEAT:*',
        'MULTIPREREQS',
        'VISIBLE',
        'DEFINE:*',
        'AUTO:ARMORPROF',
        'AUTO:EQUIP',
        'AUTO:FEAT',
        'AUTO:SHIELDPROF',
        'AUTO:WEAPONPROF:*',
        'ADDDOMAINS',
        'REMOVE',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'REP:*',
        'SPELLLIST',
        'TEMPLATE:.CLEAR',
        'TEMPLATE:*',
        @PRE_Tags,
        'PRERACETYPE',
        '!PRERACETYPE',
        'STARTSKILLPTS',
        'MODTOSKILLS',
        'SKILLLIST',
        'CSKILL:.CLEAR',
        'CSKILL',
        'CCSKILL',
        'MONSKILL',
        'MONNONSKILLHD:*',
        'SPELLLEVEL:CLASS',
        'SPELLLEVEL:DOMAIN',
    ],

    'CLASS Level' => [
        '000Level',
        'REPEATLEVEL',
        'UATT',
        'UDAM',
        'UMULT',
        'ADD:SPELLCASTER:*',
        'CAST',
        'KNOWN',
        'SPECIALTYKNOWN',
        'KNOWNSPELLS',
        'PROHIBITSPELL:*',
        'HITDIE',
        'MOVE',
        'VISION',
        'SR',
        'DR',
        'DOMAIN:*',
        'DEITY',
        'SA:.CLEAR:*',
        'SA:*',
        'BONUS:VAR:*',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DC',
        'BONUS:DR:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SLOTS',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VISION:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'TEMPDESC',
        'DEFINE:*',
        'CSKILL:.CLEAR',
        'CSKILL',
        'ADD:*',
        'ADD:CLASSSKILLS:*',
        'ADD:DOMAIN',
        'ADD:FEAT:*',
        'ADD:Language:*',
        'ADD:LIST:*',
        'ADD:SPECIAL:*',
        'ADD:VFEAT',
        'ADD:WEAPONBONUS',
        'REMOVE',
        'LANGBONUS',
        'CHOOSE',
        'EXCHANGELEVEL',
        'FEAT',
        'SPECIALS',
        'SPELL',
        'SPELLS:*',
        'TEMPLATE:.CLEAR',
        'TEMPLATE:*',
        'VFEAT:*',
        'AUTO:ARMORPROF',
        'AUTO:EQUIP',
        'AUTO:FEAT',
        'AUTO:SHIELDPROF',
        'AUTO:WEAPONPROF:*',
        'LANGAUTO',
        'ADDDOMAINS',
        'QUALIFY',
        'WEAPONBONUS',
        'FEATAUTO:.CLEAR',
        'FEATAUTO:*',
        'SUBCLASS',
        'SPELLLEVEL:CLASS',
        'SPELLLEVEL:DOMAIN',
        'SPELLLIST',
        'NATURALATTACKS',
    ],

    'COMPANIONMOD' => [
        '000Follower',
        'FOLLOWER',
        'TYPE',
        'HD',
        'DR',
        'SR',
        'VFEAT:*',
        'COPYMASTERBAB',
        'COPYMASTERCHECK',
        'COPYMASTERHP',
        'USEMASTERSKILL',
        'SA:.CLEAR',
        'SA:*',
        'DEFINE:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DC',
        'BONUS:DR:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'RACETYPE',
        'SWITCHRACE:*',
    ],

    'DEITY' => [
        '000DeityName',
        'NAMEISPI',
        'OUTPUTNAME',
        'DOMAINS',
        'FOLLOWERALIGN',
        'DESCISPI',
        'DESC',
        'SYMBOL',
        'DEITYWEAP',
        'ALIGN',
        'PANTHEON',
        'TITLE',
        'WORSHIPPERS',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE',
        @PRE_Tags,
        'BONUS:CHECKS:*',
        'BONUS:CASTERLEVEL:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DC',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'DEFINE:*',
        'SR',
        'DR',
        'VFEAT:*',
        'SA:.CLEAR',
        'SA:*',
    ],

    'DOMAIN' => [
        '000DomainName',
        'NAMEISPI',
        'OUTPUTNAME',
        @PRE_Tags,
        'CSKILL:.CLEAR',
        'CSKILL',
        'CCSKILL',
        'CHOOSE',
        'SPELL',
        'SPELLS:*',
        'VISION',
        'SR',
        'DR',
        'FEAT',
        'VFEAT:*',
        'ADD:FEAT:*',
        'AUTO:ARMORPROF',
        'AUTO:EQUIP',
        'AUTO:FEAT',
        'AUTO:SHIELDPROF',
        'AUTO:WEAPONPROF:*',
        'FEATAUTO',
        'SA:*',
        'DEFINE:*',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DC',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE',
        'DESCISPI',
        'DESC',
        'SPELLLEVEL:DOMAIN',
    ],

    'EQUIPMENT' => [
        '000EquipmentName',
        'NAMEISPI',
        'OUTPUTNAME',
        'PROFICIENCY',
        'TYPE:.CLEAR',
        'TYPE:*',
        'ALTTYPE',
        'CONTAINS',
        'COST',
        'WT',
        'SLOTS',
        @PRE_Tags,
        'DEFINE:*',
        'ACCHECK',
        'BASEITEM',
        'BASEQTY',
        'CHOOSE',
        'CRITMULT',
        'CRITRANGE',
        'ALTCRITICAL',
        'ALTCRITRANGE',
        'FUMBLERANGE',
        'DAMAGE',
        'ALTDAMAGE',
        'EQMOD',
        'ALTEQMOD',
        'HANDS',
        'WIELD',
        'MAXDEX',
        'MODS',
        'RANGE',
        'REACH',
        'SIZE',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE:.CLEAR',
        'SOURCEPAGE',
        'SPELLFAILURE',
        'ADD:DOMAIN',
        'ADD:FEAT',
        'VFEAT:*',
        'VISION',
        'SR',
        'DR',
        'SPELL:*',
        'SPELLS:*',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DC',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SLOTS',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'LANGAUTO',
        'SPROP:*',
        'SA:.CLEAR',
        'SA:*',
        'CSKILL:.CLEAR',
        'CSKILL',
        'RATEOFFIRE',
        'AUTO:EQUIP',
        'ADD:SPELLCASTER',
        'DESC',
    ],

    'EQUIPMOD' => [
        '000ModifierName',
        'NAMEISPI',
        'OUTPUTNAME',
        'KEY',
        'TYPE',
        'PLUS',
        'COST',
        'VISIBLE',
        'ITYPE',
        'IGNORES',
        'REPLACES',
        'COSTPRE',
        'NAMEOPT',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE',
        @PRE_Tags,
        'ADDPROF',
        'VISION',
        'SR',
        'DR',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DC',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:ITEMCOST:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'SPROP:*',
        'VFEAT:*',
        'FUMBLERANGE',
        'SA:.CLEAR',
        'SA:*',
        'ARMORTYPE:*',
        'CHOOSE',
        'ASSIGNTOALL',
        'CHARGES',
        'DEFINE:*',
        'SPELL',
        'SPELLS:*',
        'RATEOFFIRE',
        'AUTO:EQUIP',
    ],

    'FEAT' => [
        '000FeatName',
        'NAMEISPI',
        'OUTPUTNAME',
        'TYPE:.CLEAR',
        'TYPE',
        'VISIBLE',
        @PRE_Tags,
        'SA:.CLEAR',
        'SA:*',
        'DEFINE:*',
        'SPELL:*',
        'SPELLS:*',
        'DESCISPI',
        'DESC',
        'STACK',
        'MULT',
        'CHOOSE',
        'TEMPLATE:.CLEAR',
        'TEMPLATE:*',
        'MOVE',
        'MOVECLONE',
        'AUTO:ARMORPROF',
        'AUTO:EQUIP',
        'AUTO:FEAT',
        'AUTO:SHIELDPROF',
        'AUTO:WEAPONPROF:*',
        'UDAM',
        'VFEAT:*',
        'ADD',
        'ADD:CLASSSKILLS',
        'ADD:FAVOREDCLASS',
        'ADD:FEAT:*',
        'ADD:FORCEPOINT',
        'ADD:Language',
        'ADD:LIST',
        'ADD:SPECIAL',
        'ADD:SPELLCASTER',
        'ADD:SKILL',
        'ADD:WEAPONPROFS',
        'ADDSPELLLEVEL',
        'LANGAUTO',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DC:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SLOTS:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'CSKILL:.CLEAR',
        'CSKILL',
        'CCSKILL',
        'VISION',
        'SR',
        'DR',
        'REP',
        'COST',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE:.CLEAR',
        'SOURCEPAGE',
        'NATURALATTACKS',
        'BENEFIT',
        'TEMPDESC',
        'SPELLLEVEL:CLASS:*',
        'SPELLLEVEL:DOMAIN:*',
    ],

    'KIT ALIGN' => [
        'ALIGN',
        'PRERACE',
    ],

    'KIT CLASS' => [
        'CLASS',
        'LEVEL',
        'SUBCLASS',
    ],

    'KIT DEITY' => [
        'DEITY',
        'DOMAIN',
    ],

    'KIT FEAT' => [
        'FEAT',
        'PRERACE',
        'PRESTAT',
        'FREE',
    ],

    'KIT GEAR' => [
        'GEAR',
        'QTY',
        'SIZE',
        'MAXCOST',
        'LOCATION',
        'EQMOD',
        'LEVEL',
        'SPROP',
        'PREDEFAULTMONSTER',
        'PRERACE',
    ],

    'KIT KIT' => [
        'KIT',
    ],

    'KIT LANGAUTO' => [
        'LANGAUTO',
    ],

    'KIT NAME' => [
        'NAME',
    ],

    'KIT PROF' => [
        'PROF',
        'RACIAL',
        'PRERACE',
        'PREMULT',
    ],

    'KIT RACE' => [
        'RACE',
        'PRERACE',
    ],

    'KIT REGION' => [
        'REGION',
    ],

    'KIT SKILL' => [
        'SKILL',
        'RANK',
        'FREE',
        'COUNT',
    ],

    'KIT SPELLS' => [
        'SPELLS',
        'COUNT',
    ],

    'KIT STARTPACK' => [
        'STARTPACK',
        'VISIBLE',
        'EQUIPBUY',
        'EQUIPSELL',
        @PRE_Tags,
        'SOURCEPAGE',
    ],

    'KIT STAT' => [
        'STAT',
    ],

    'KIT TEMPLATE' => [
        'TEMPLATE',
    ],

    'LANGUAGE' => [
        '000LanguageName',
        'NAMEISPI',
        'TYPE',
        'SOURCEPAGE',
        @PRE_Tags,
    ],

    'MASTERBONUSRACE' => [
        '000MasterBonusRace',
        'TYPE',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DC',
        'BONUS:FEAT:*',
        'BONUS:MOVEADD:*',
        'BONUS:HP',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:SKILL:*',
        'BONUS:STAT:*',
        'BONUS:UDAM:*',
        'VFEAT:*',
        'SA',
    ],

    'PCC' => [
        'CAMPAIGN',
        'GAMEMODE',
        'GENRE',
        'BOOKTYPE',
        'PUBNAMELONG',
        'PUBNAMESHORT',
        'PUBNAMEWEB',
        'SETTING',
        'TYPE',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'COPYRIGHT',
        'LICENSE',
        'HELP',
        'INFOTEXT',
        'ISD20',
        'ISLICENSED',
        'ISOGL',
        'BIOSET',
        'HIDETYPE',

        # These tags load files
        'CLASS',
        'CLASSSKILL',
        'CLASSSPELL',
        'COMPANIONMOD',
        'DEITY',
        'DOMAIN',
        'EQUIPMENT',
        'EQUIPMOD',
        'FEAT',
        'KIT',
        'LANGUAGE',
        'LSTEXCLUDE',
        'PCC',
        'RACE',
        'RANK',
        'REQSKILL',
        'SKILL',
        'SPELL',
        'TEMPLATE:.CLEAR',
        'TEMPLATE:*',
        'WEAPONPROF',
        '#EXTRAFILE',
    ],

    'RACE' => [
        '000RaceName',
        'NAMEISPI',
        'OUTPUTNAME',
        'FAVCLASS',
        'XTRASKILLPTSPERLVL',
        'STARTFEATS',
        'SIZE',
        'MOVE',
        'UNENCUMBEREDMOVE',
        'FACE',
        'REACH',
        'VISION',
        @PRE_Tags,
        'LANGAUTO',
        'LANGBONUS:.CLEAR',
        'LANGBONUS',
        'WEAPONBONUS:*',
        'CHANGEPROF',
        'PROF',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DC',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SLOTS:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WIELDCATEGORY:*',
        'CSKILL:.CLEAR',
        'CSKILL',
        'CCSKILL',
        'MONCSKILL',
        'MONCCSKILL',
        'AUTO:ARMORPROF',
        'AUTO:EQUIP',
        'AUTO:FEAT',
        'AUTO:SHIELDPROF',
        'AUTO:WEAPONPROF:*',
        'VFEAT:*',
        'FEAT',
        'MFEAT',
        'LEGS',
        'HANDS',
        'BONUS:WEAPONPROF:*',
        'NATURALATTACKS',
        'SA:.CLEAR',
        'SA:*',
        'DEFINE:*',
        'HITDICE',
        'SR',
        'DR',
        'SKILLMULT',
        'BAB',
        'HITDIE',
        'MONSTERCLASS',
        'RACETYPE',
        'RACESUBTYPE',
        'TYPE',
        'TEMPLATE:.CLEAR',
        'TEMPLATE:*',
        'HITDICEADVANCEMENT',
        'LEVELADJUSTMENT',
        'CR',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE',
        'SPELL:*',
        'SPELLS:*',
        'CHOOSE',
        'ADD:FEAT:*',
        'ADD:SPELLCASTER:*',
        'REGION',
        'SPELLLEVEL:DOMAIN:*',
        'KIT',
    ],

    'SKILL' => [
        '000SkillName',
        'NAMEISPI',
        'OUTPUTNAME',
        'KEYSTAT',
        'USEUNTRAINED',
        'ACHECK',
        'EXCLUSIVE',
        'CLASSES',
        'TYPE',
        'VISIBLE',
        @PRE_Tags,
        'BONUS:SKILL:*',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE',
        'CHOOSE',
        'DEFINE',
        'VFEAT:*',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DC',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'REQ',
        'SA',
        'TEMPDESC',
    ],

    'SOURCE' => [
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
    ],

    'SPELL' => [
        '000SpellName',
        'NAMEISPI',
        'OUTPUTNAME',
        'CLASSES',
        'DOMAINS',
        'STAT:*',
        'SCHOOL:.CLEAR',
        'SCHOOL',
        'SUBSCHOOL',
        'DESCRIPTOR',
        'VARIANTS',
        'TYPE',
        'COMPS',
        'CASTTIME',
        'RANGE:.CLEAR',
        'RANGE',
        'ITEM:*',
        'TARGETAREA',
        'DURATION',
        'CT',
        'SAVEINFO',
        'SPELLRES',
        'COST',
        'XPCOST',
        @PRE_Tags,
        'DESCISPI',
        'DESC',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:FEAT:*',
        'BONUS:HP',
        'BONUS:MOVEADD',
        'BONUS:MOVEMULT',
        'BONUS:POSTMOVEADD',
        'BONUS:SIZEMOD',
        'BONUS:SKILL:*',
        'BONUS:STAT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR',
        'BONUS:WEAPON',
        'BONUS:WIELDCATEGORY:*',
        'DR',
        'MULT',
        'CHOOSE',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE:.CLEAR',
        'SOURCEPAGE',
        'TEMPDESC',
    ],

    'SUBCLASS' => [
        '000SubClassName',
        'NAMEISPI',
        'OUTPUTNAME',
        'HD',
        'ABB',
        'COST',
        'PROHIBITCOST',
        'CHOICE',
        'SPELLSTAT',
        'SPELLTYPE',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DC:*',
        'BONUS:FEAT:*',
        'BONUS:SKILL:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WIELDCATEGORY:*',
        'SPELLLIST',
        'KNOWNSPELLSFROMSPECIALTY',
        'PROHIBITED',
        'SA',
        'DEFINE',
        @PRE_Tags,
        'CSKILL:.CLEAR',
        'CSKILL',
        'ADDDOMAINS',
        'SOURCEPAGE',
    ],

    'SUBCLASSLEVEL' => [
        'SUBCLASSLEVEL',
        'REPEATLEVEL',
        'UATT',
        'UDAM',
        'UMULT',
        'ADD:SPELLCASTER:*',
        'SPELLLEVEL:CLASS:*',
        'CAST',
        'KNOWN',
        'SPECIALTYKNOWN',
        'KNOWNSPELLS',
        'VISION',
        'SR',
        'DR',
        'DOMAIN',
        'SA:.CLEAR:*',
        'SA:*',
        'BONUS:VAR:*',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DC',
        'BONUS:DR:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'DEFINE:*',
        'CSKILL:.CLEAR',
        'CSKILL',
        'ADD:*',
        'ADD:CLASSSKILLS:*',
        'ADD:DOMAIN',
        'ADD:FEAT:*',
        'ADD:Language:*',
        'ADD:LIST:*',
        'ADD:SPECIAL:*',
        'ADD:WEAPONBONUS',
        'EXCHANGELEVEL',
        'SPECIALS',
        'SPELL',
        'SPELLS:*',
        'TEMPLATE:.CLEAR',
        'TEMPLATE:*',
        'VFEAT:*',
        'AUTO:ARMORPROF',
        'AUTO:EQUIP',
        'AUTO:FEAT',
        'AUTO:SHIELDPROF',
        'AUTO:WEAPONPROF:*',
        'ADDDOMAINS',
        'WEAPONBONUS',
        'FEATAUTO:.CLEAR',
        'FEATAUTO:*',
        'SUBCLASS',
        'SPELLLIST',
        'NATURALATTACKS',
    ],

    'SWITCHRACE' => [
        'SWITCHRACE',
    ],

    'TEMPLATE' => [
        '000TemplateName',
        'NAMEISPI',
        'OUTPUTNAME',
        'HITDIE',
        'HITDICESIZE',
        'CR',
        'SIZE',
        'VISIBLE',
        'REMOVEABLE',
        'DR:*',
        'LEVELADJUSTMENT',
        'TEMPLATE:.CLEAR',
        'TEMPLATE:*',
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE',
        'SA:.CLEAR',
        'SA:*',
        'DEFINE:*',
        'LEVEL:*',
        @PRE_Tags,
        'QUALIFY',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DC',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SLOTS:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'BONUSFEATS',
        'BONUSSKILLPOINTS',
        'NONPP',
        'CHOOSE',
        'CSKILL:.CLEAR',
        'CSKILL',
        'ADD:CLASSSKILLS',
        'ADD:EQUIP',
        'ADD:FEAT:*',
        'ADD:Language',
        'ADD:VFEAT',
        'FAVOREDCLASS',
        'FEAT:*',
        'VFEAT:*',
        'AUTO:ARMORPROF',
        'AUTO:EQUIP',
        'AUTO:FEAT',
        'AUTO:SHIELDPROF',
        'AUTO:WEAPONPROF:*',
        'REMOVE',
        'CHANGEPROF',
        'HEIGHT',
        'KIT',
        'LANGAUTO:.CLEAR',
        'LANGAUTO:*',
        'LANGBONUS',
        'MOVE',
        'MOVEA',
        'MOVECLONE',
        'REGION',
        'SUBREGION',
        'REMOVABLE',
        'SR:*',
        'SUBRACE',
        'RACETYPE',
        'RACESUBTYPE',
        'TYPE',
        'VISION',
        'WEIGHT',
        'HD:*',
        'WEAPONBONUS',
        'GENDERLOCK',
        'SPELL:*',
        'SPELLS:*',
        'SPELLLEVEL:CLASS:*',
        'ADD:SPELLCASTER',
        'NATURALATTACKS',
        'TEMPDESC',
    ],

    'WEAPONPROF' => [
        '000WeaponName',
        'NAMEISPI',
        'OUTPUTNAME',
        'TYPE',
        'HANDS',
        @PRE_Tags,
        'SOURCELONG',
        'SOURCESHORT',
        'SOURCEWEB',
        'SOURCEPAGE',
        'BONUS:CASTERLEVEL:*',
        'BONUS:CHECKS:*',
        'BONUS:COMBAT:*',
        'BONUS:DAMAGE:*',
        'BONUS:DEFINE:*',
        'BONUS:DOMAIN:*',
        'BONUS:DC',
        'BONUS:DR:*',
        'BONUS:EQM:*',
        'BONUS:EQMARMOR:*',
        'BONUS:EQMWEAPON:*',
        'BONUS:ESIZE:*',
        'BONUS:FEAT:*',
        'BONUS:HD:*',
        'BONUS:HP:*',
        'BONUS:LANGUAGES:*',
        'BONUS:MISC:*',
        'BONUS:MOVEADD:*',
        'BONUS:MOVEMULT:*',
        'BONUS:POSTMOVEADD:*',
        'BONUS:POSTRANGEADD:*',
        'BONUS:PCLEVEL:*',
        'BONUS:RANGEADD:*',
        'BONUS:RANGEMULT:*',
        'BONUS:REPUTATION:*',
        'BONUS:RING:*',
        'BONUS:SCHOOL:*',
        'BONUS:SIZEMOD:*',
        'BONUS:SKILL:*',
        'BONUS:SKILLPOINTS:*',
        'BONUS:SKILLPOOL:*',
        'BONUS:SKILLRANK:*',
        'BONUS:SPELL:*',
        'BONUS:SPELLCAST:*',
        'BONUS:SPELLCASTMULT:*',
        'BONUS:SPELLKNOWN:*',
        'BONUS:VISION:*',
        'BONUS:STAT:*',
        'BONUS:TOHIT:*',
        'BONUS:UDAM:*',
        'BONUS:VAR:*',
        'BONUS:WEAPON:*',
        'BONUS:WEAPONPROF:*',
        'BONUS:WIELDCATEGORY:*',
        'SA:.CLEAR',
        'SA:*',
    ],
);

#################################################################
######################## Conversion #############################
# Tags that must be seen as valid to allow convertion.

if ( $conversion_enable{'BIOSET:generate the new files'} ) {
    push @{ $master_order{'RACE'} }, 'AGE', 'HEIGHT', 'WEIGHT';
}

if ( $conversion_enable{'EQUIPMENT: remove ATTACKS'} ) {
    push @{ $master_order{'EQUIPMENT'} }, 'ATTACKS';
}

if ( $conversion_enable{'PCC:GAME to GAMEMODE'} ) {
    push @{ $master_order{'PCC'} }, 'GAME';
}

if ( $conversion_enable{'ALL:BONUS:MOVE convertion'} ) {
    push @{ $master_order{'CLASS'} },         'BONUS:MOVE:*';
    push @{ $master_order{'CLASS Level'} },   'BONUS:MOVE:*';
    push @{ $master_order{'COMPANIONMOD'} },  'BONUS:MOVE:*';
    push @{ $master_order{'DEITY'} },         'BONUS:MOVE:*';
    push @{ $master_order{'DOMAIN'} },        'BONUS:MOVE:*';
    push @{ $master_order{'EQUIPMENT'} },     'BONUS:MOVE:*';
    push @{ $master_order{'EQUIPMOD'} },      'BONUS:MOVE:*';
    push @{ $master_order{'FEAT'} },          'BONUS:MOVE:*';
    push @{ $master_order{'RACE'} },          'BONUS:MOVE:*';
    push @{ $master_order{'SKILL'} },         'BONUS:MOVE:*';
    push @{ $master_order{'SUBCLASSLEVEL'} }, 'BONUS:MOVE:*';
    push @{ $master_order{'TEMPLATE'} },      'BONUS:MOVE:*';
    push @{ $master_order{'WEAPONPROF'} },    'BONUS:MOVE:*';
}

if ( $conversion_enable{'WEAPONPROF:No more SIZE'} ) {
    push @{ $master_order{'WEAPONPROF'} }, 'SIZE';
}

if ( $conversion_enable{'EQUIP:no more MOVE'} ) {
    push @{ $master_order{'EQUIPMENT'} }, 'MOVE';
}

#   vvvvvv This one is disactivated
if ( 0 && $conversion_enable{'ALL:Convert SPELL to SPELLS'} ) {
    push @{ $master_order{'CLASS Level'} },   'SPELL:*';
    push @{ $master_order{'DOMAIN'} },        'SPELL:*';
    push @{ $master_order{'EQUIPMOD'} },      'SPELL:*';
    push @{ $master_order{'SUBCLASSLEVEL'} }, 'SPELL:*';
}

#   vvvvvv This one is disactivated
if ( 0 && $conversion_enable{'TEMPLATE:HITDICESIZE to HITDIE'} ) {
    push @{ $master_order{'TEMPLATE'} }, 'HITDICESIZE';
}

# Working variables
my %column_with_no_tag = (

    'CLASS' => [
        '000ClassName',
    ],

    'CLASS Level' => [
        '000Level',
    ],

    'COMPANIONMOD' => [
        '000Follower',
    ],

    'DEITY' => [
        '000DeityName',
    ],

    'DOMAIN' => [
        '000DomainName',
    ],

    'EQUIPMENT' => [
        '000EquipmentName',
    ],

    'EQUIPMOD' => [
        '000ModifierName',
    ],

    'FEAT' => [
        '000FeatName',
    ],

    'LANGUAGE' => [
        '000LanguageName',
    ],

    'MASTERBONUSRACE' => [
        '000MasterBonusRace',
    ],

    'RACE' => [
        '000RaceName',
    ],

    'SKILL' => [
        '000SkillName',
    ],

    'SPELL' => [
        '000SpellName',
    ],

    'SUBCLASS' => [
        '000SubClassName',
    ],

    'TEMPLATE' => [
        '000TemplateName',
    ],

    'WEAPONPROF' => [
        '000WeaponName',
    ],

);

my %token_ADD_tag = map { $_ => 1 } (
    'ADD:CLASSSKILLS',
    'ADD:DOMAIN',
    'ADD:EQUIP',
    'ADD:FAVOREDCLASS',
    'ADD:FEAT',
    'ADD:FORCEPOINT',
    'ADD:INIT',
    'ADD:Language',
    'ADD:LIST',
    'ADD:SPECIAL',
    'ADD:SPELLCASTER',
    'ADD:SKILL',
    'ADD:TEMPLATE',
    'ADD:WEAPONBONUS',
    'ADD:WEAPONPROFS',
    'ADD:VFEAT',
);

my %token_BONUS_tag = map { $_ => 1 } (
    'CASTERLEVEL',
    'CHECKS',
    'COMBAT',
    'DAMAGE',
    'DC',
    'DOMAIN',
    'DR',
    'EQM',
    'EQMARMOR',
    'EQMWEAPON',
    'ESIZE',
    'FEAT',
    'HD',
    'HP',
    'ITEMCOST',
    'LANGUAGES',
    'MISC',
    'MOVE',
    'MOVEADD',
    'MOVEMULT',
    'POSTRANGEADD',
    'POSTMOVEADD',
    'PCLEVEL',
    'RANGEADD',
    'RANGEMULT',
    'REPUTATION',
    'SIZEMOD',
    'SKILL',
    'SKILLPOINTS',
    'SKILLPOOL',
    'SKILLRANK',
    'SLOTS',
    'SPELL',
    'SPELLCAST',
    'SPELLCASTMULT',
    'SPELLKNOWN',
    'VISION',
    'STAT',
    'TOHIT',
    'UDAM',
    'VAR',
    'WEAPON',
    'WEAPONPROF',
    'WIELDCATEGORY',
);

# List of types that are valid in BONUS:SLOTS
my %token_BONUS_SLOTS_types = map { $_ => 1 } (
    'AMULET',
    'BELT',
    'BOOT',
    'BRACER',
    'CAPE',
    'EYEGEAR',
    'GLOVE',
    'HANDS',
    'HEADGEAR',
    'LEGS',
    'ROBE',
    'RING',
    'SHIRT',
    'SUIT',

    # Special value for the CHOOSE tag
    'LIST',
);

# [ 832171 ] AUTO:* needs to be separate tags
my @token_AUTO_tag = (
    'ARMORPROF',
    'EQUIP',
    'FEAT',
    'SHIELDPROF',
    'WEAPONPROF',
);

# Add the CHOOSE type.
# CHOOSE:xxx will not becaume separate tags but we need to be able to
# validate the different CHOOSE types.
my %token_CHOOSE_tag = map { $_ => 1 } (
    'CCSKILLLIST',
    'COUNT',
    'FEAT',
    'FEATLIST',
    'HP',
    'NUMBER',
    'SCHOOLS',
    'SKILL',
    'SKILLSNAMED',
    'SPELLLEVEL',
    'SPELLLIST',
    'WEAPONPROFS',
);

my %master_mult;        # Will hold the tags that can be there more then once

my %valid_tags;         # Will hold the valid tags for each type of file.

my %count_tags;         # Will hold the number of each tag found (by linetype)

my %missing_headers;    # Will hold the tags that do not have defined headers
                        # for each linetype.

################################################################################
# Global variables used by the validation code

my %valid_entities;     # Will hold the entries that may be refered
                        # by other tags
                        # Format $valid_entities{$entitytype}{$entityname}
                        # We initialise the hash with global system values
                        # that are valid but never defined in the .lst files.

my %valid_types;        # Will hold the valid types for the TYPE. or TYPE=
                        # found in different tags.
                        # Format valid_types{$entitytype}{$typename}

my %referer;            # Will hold the tags the refer to other entries
                        # Format: push @{$referer{$EntityType}{$entryname}},
                        #         [ $tags{$column}, $file_for_error, $line_for_error ]

my %referer_types;      # Will hold the type used by some of the tags
                        # to allow validation.
                        # Format: push @{$referer_types{$EntityType}{$typename}},
                        #         [ $tags{$column}, $file_for_error, $line_for_error ]

my %valid_sub_entities; # Will hold the entities that are allowed to include
                        # a sub-entity between () in their name.
                        # e.g. Skill Focus(Spellcraft)
                        # Format: $valid_sub_entities{$entity_type}{$entity_name}
                        #           = $sub_entity_type;
                        # e.g. :  $valid_sub_entities{'FEAT'}{'Skill Focus'} = 'SKILL';

my @xcheck_to_process;  # Will hold the information for the entries that must
                        # be added in %referer or %referer_types. The array
                        # is needed because all the files must have been
                        # parsed before processing the information to be added.
                        # The function add_to_xcheck_tables will be called with
                        # each line of the array.

# Add pre-defined valid entities
for my $var_name (@valid_system_var_names) {
    $valid_entities{'DEFINE Variable'}{$var_name}++;
}

for my $stat (@valid_system_stats) {
    $valid_entities{'DEFINE Variable'}{ $stat           }++;
    $valid_entities{'DEFINE Variable'}{ $stat . 'SCORE' }++;
}

################################################################################

# Header use for the comment for each of the tag used in the script
my %tagheader = (
    default => {
        '000ClassName'          => '# Class Name',
        '001SkillName'          => 'Class Skills (All skills are seperated by a pipe delimiter \'|\')',

        '000DomainName'         => '# Domain Name',
        '001DomainEffect'       => 'Description',

        'DESC'                  => 'Description',

        '000FeatName'           => '# Feat Name',

        '000LanguageName'       => '# Language',

        'FAVCLASS'              => 'Favored Class',
        'XTRASKILLPTSPERLVL'    => 'Skills/Level',
        'STARTFEATS'            => 'Starting Feats',

        '000SkillName'          => '# Skill Name',

        'KEYSTAT'               => 'Key Stat',
        'EXCLUSIVE'             => 'Exclusive?',
        'USEUNTRAINED'          => 'Untrained?',

        '000TemplateName'       => '# Template Name',

        '000WeaponName'         => '# Weapon Name',

        'ACCHECK'               => 'AC Penalty Check',
        'ACHECK'                => 'Skill Penalty Apply',
        'ADD'                   => 'Add',
        'ADD:EQUIP'             => 'Choose an Equipment',
        'ADD:FEAT'              => 'Choose a Feat',
        'ADD:SKILL'             => 'Choose a Skill',
        'ADDDOMAINS'            => 'Additional Devine Domain',
        'ADDSPELLLEVEL'         => 'Add Spell Lvl',
        'AGE'                   => 'Age',
        'AGESET'                => 'Age Set',
        'ALTCRITICAL'           => 'Alternative Critical',
        'ALTCRITRANGE'          => 'Alternative Critical Range',
        'ALTDAMAGE'             => 'Alternative Damage',
        'ALTEQMOD'              => 'Alternative Modifier',
        'ALTTYPE'               => 'Alternative Type',
        'ATTACKCYCLE'           => 'Attack Cycle',
        'AUTO'                  => 'Auto',
        'AUTO:ARMORPROF'        => 'Automaticaly Gained Armor Prof.',
        'AUTO:EQUIP'            => 'Automaticaly Added to Inventory',
        'AUTO:FEAT'             => 'Automaticaly Gained Feat',
        'AUTO:SHIELDPROF'       => 'Automaticaly Gained Shield Prof.',
        'AUTO:WEAPONPROF'       => 'Automaticaly Gained Weapon Prof.',
        'BASEQTY'               => 'Base Quantity',
        'BENEFIT'               => 'Description of the Benefits',
        'BONUS'                 => 'Bonus',
        'BONUSSPELLSTAT'        => 'Stat for Bonus Spells',
        'BONUS:CASTERLEVEL'     => 'Caster level',
        'BONUS:CHECKS'          => 'Save checks bonus',
        'BONUS:COMBAT'          => 'Combat bonus',
        'BONUS:DAMAGE'          => 'Weapon damage bonus',
        'BONUS:DOMAIN'          => 'Add domain number',
        'BONUS:DR'              => 'Damage reduction',
        'BONUS:ESIZE'           => 'Modify size',
        'BONUS:HD'              => 'Modify HD type',
        'BONUS:HP'              => 'Bonus to HP',
        'BONUS:ITEMCOST'        => 'Modify the item cost',
        'BONUS:LANGUAGES'       => 'More language',
        'BONUS:MISC'            => 'Misc bonus',
        'BONUS:MOVE'            => 'Move class',
        'BONUS:MOVEADD'         => 'Add to base move',
        'BONUS:MOVEMULT'        => 'Multiply base more',
        'BONUS:POSTMOVEADD'     => 'Add to magical move',
        'BONUS:PCLEVEL'         => 'Caster level bonus',
        'BONUS:POSTRANGEADD'    => 'BONUS:EQMWEAPON',
        'BONUS:RANGEADD'        => 'BONUS:RANGEADD',
        'BONUS:RANGEMULT'       => '% bonus to range',
        'BONUS:REPUTATION'      => 'BONUS:REPUTATION',
        'BONUS:SKILL'           => 'Bonus to skill',
        'BONUS:SKILLPOINTS'     => 'Bonus to skill point/L',
        'BONUS:SKILLPOOL'       => 'Bonus to skill point for a level',
        'BONUS:SKILLRANK'       => 'Bonus to skill rank',
        'BONUS:SLOTS'           => 'Bonus to nb of slots',
        'BONUS:SPELL'           => 'Bonus to spell attribute',
        'BONUS:SPELLCAST'       => 'Bonus to spell cast/day',
        'BONUS:SPELLCASTMULT'   => 'Multiply spell cast/day',
        'BONUS:SPELLKNOWN'      => 'Bonus to spell known/L',
        'BONUS:STAT'            => 'Stat bonus',
        'BONUS:TOHIT'           => 'Attack roll bonus',
        'BONUS:UDAM'            => 'Unarmed Damage Level bonus',
        'BONUS:VAR'             => 'Modify VAR',
        'BONUS:VISION'          => 'Add to vision',
        'BONUS:WEAPON'          => 'Weapon prop. bonus',
        'BONUS:WEAPONPROF'      => 'Weapon prof. bonus',
        'BONUS:WIELDCATEGORY'   => 'Wield Category bonus',
        'CAST'                  => 'Cast',
        'CASTAS'                => 'Cast As',
        'CASTTIME'              => 'Casting Time',
        'CCSKILL'               => 'Cross-class Skill',
        'CHANGEPROF'            => 'Change Weapon Prof. Category',
        'CHOOSE'                => 'Choose',
        'CLASSES'               => 'Classes',
        'COMPS'                 => 'Components',
        'CONTAINS'              => 'Contains',
        'COST'                  => 'Cost',
        'CRITMULT'              => 'Critical Hit Multiplier',
        'CRITRANGE'             => 'Critical Hit Range',
        'CSKILL:.CLEAR'         => 'Removed Class Skill',
        'CSKILL'                => 'Class Skill',
        'CT'                    => 'Casting Threshold',
        'DAMAGE'                => 'Damage',
        'DEF',                  => 'Def',
        'DEFINE'                => 'Define',
        'DEITY'                 => 'Deity',
        'DESC'                  => 'Description',
        'DESCISPI'              => 'Desc is PI?',
        'DESCRIPTOR'            => 'Spell Descriptors',
        'DOMAINS'               => 'Domains',
        'DR'                    => 'Damage Reduction',
        'DURATION'              => 'Duration',
        'EFFECTS'               => 'Description',
        'EQMOD'                 => 'Modifier',
        'EXCLASS'               => 'Ex Class',
        'FACE'                  => 'Face/Space',
        'FEAT'                  => 'Feat',
        'FEATAUTO'              => 'Feat Auto',
        'FREE',                 => 'Free',
        'FUMBLERANGE'           => 'Fumble Range',
        'HANDS'                 => 'Nb Hands',
        'HASSUBCLASS'           => 'Subclass?',
        'HD'                    => 'Hit Dice',
        'HEIGHT'                => 'Height',
        'HITDIE'                => 'Hit Dice Size',
        'HITDICEADVANCEMENT'    => 'Hit Dice Advancement',
        'HITDICESIZE'           => 'Hit Dice Size',
        'ITEM',                 => 'Item',
        'KNOWN'                 => 'Known',
        'KNOWNSPELLS'           => 'Automaticaly Known Spell Levels',
        'LANGAUTO'              => 'Automatic Languages',
        'LANGBONUS'             => 'Bonus Languages',
        'LANGBONUS:.CLEAR'      => 'Clear Bonus Languages',
        'LEGS'                  => 'Nb Legs',
        'LEVEL',                => 'Level',
        'LEVELADJUSTMENT'       => 'Level Adjustment',
        'LONGNAME'              => 'Long Name',
        'MAXCOST'               => 'Maximum Cost',
        'MAXDEX'                => 'Maximum DEX Bonus',
        'MAXLEVEL'              => 'Max Level',
        'MEMORIZE'              => 'Memorize',
        'MFEAT'                 => 'Default Monster Feat',
        'MONSKILL'              => 'Monster Initial Skill Points',
        'MOVE'                  => 'Move',
        'MULT'                  => 'Multiple?',
        'NAMEISPI'              => 'Product Identity?',
        'NATURALARMOR'          => 'Natural Armor',
        'NATURALATTACKS'        => 'Natural Attacks',
        'OUTPUTNAME'            => 'Output Name',
        'PRE:.CLEAR',           => 'Clear Prereq.',
        'PREALIGN'              => 'Required AL',
        '!PREALIGN'             => 'Restricted AL',
        'PREATT'                => 'Req. Att.',
        'PREARMORPROF'          => 'Req. Armor Prof.',
        '!PREARMORPROF'         => 'Prohibited Armor Prof.',
        'PREBASESIZEEQ'         => 'Req. Base Size',
        'PREBASESIZEGT'         => 'PREBASESIZEGT',
        'PREBASESIZEGTEQ'       => 'Minimum Size',
        'PREBASESIZELT'         => 'PREBASESIZELT',
        'PREBASESIZELTEQ'       => 'Maximum Size',
        'PREBASESIZENEQ'        => 'PREBASESIZENEQ',
        'PRECHECK'              => 'Required Check',
        'PRECHECKBASE'          => 'Required Check Base',
        'PRECLASS'              => 'Required Class',
        '!PRECLASS'             => 'Prohibited Class',
        'PRECLASSLEVELMAX'      => 'Maximum Level Allowed',
        '!PRECLASSLEVELMAX'     => 'Should use PRECLASS',
        'PRECSKILL'             => 'Required Class Skill',
        '!PRECSKILL'            => 'Prohibited Class SKill',
        'PREDEITY'              => 'Required Deity',
        'PREDEITYDOMAIN'        => 'Required Domain',
        'PREDSIDEPTS'           => 'Req. Dark Side',
        'PREDR'                 => 'Req. Damage Resistance',
        'PREEQUIP'              => 'Req. Equipement',
        'PREEQMOD'              => 'Req. Equipment Mod.',
        '!PREEQMOD'             => 'Prohibited Equipment Mod.',
        'PREFEAT'               => 'Required Feat',
        '!PREFEAT'              => 'Prohibited Feat',
        'PREGENDER'             => 'Required Gender',
        'PREHANDSEQ'            => 'Req. nb of Hand',
        'PREHANDSGT'            => 'Min. nb of Hand',
        'PREITEM'               => 'Required Item',
        'PRELANG'               => 'Required Language',
        'PRELEVEL'              => 'Required Lvl',
        'PRELEVELMAX'           => 'Maximum Level',
        'PREMOVE'               => 'Req. Movement',
        'PREMULT',              => 'Multiple Requirements',
        '!PREMULT'              => 'Multiple Prohibitions',
        'PRERACE'               => 'Required Race',
        '!PRERACE'              => 'Prohibited Race',
        'PRERACETYPE'           => 'Reg. Race Type',
        'PREREGION'             => 'Required Region',
        'PRERULE'               => 'Req. Rule (in options)',
        'PRESA'                 => 'Req. Special Ability',
        'PRESHIELDPROF'         => 'Req. Shield Prof.',
        '!PRESHIELDPROF'        => 'Prohibited Shield Prof.',
        'PRESIZEEQ'             => 'Required Size',
        'PRESIZEGTEQ'           => 'Minimum Size',
        'PRESIZELT'             => 'Must be Smaller',
        'PRESIZELTEQ'           => 'Maximum Size',
        'PRESKILL'              => 'Required Skill',
        'PRESKILLMULT'          => 'Special Required Skill',
        'PRESKILLTOT'           => 'Total Skill Points Req.',
        'PRESPELL'              => 'Req. Known Spell',
        'PRESPELLBOOK'          => 'Req. Spellbook',
        'PRESPELLCAST'          => 'Req. Casting Type',
        'PRESPELLDESCRIPTOR'    => 'Req. Spell Descriptor',
        'PRESPELLSCHOOL'        => 'Required Spell School',
        'PRESPELLSCHOOLSUB'     => 'Required Sub-school',
        '!PRESPELLSCHOOLSUB'    => 'Prohibited Sub-school',
        'PRESPELLTYPE'          => 'Req. Spell Type',
        'PRESTAT'               => 'Required Stat',
        '!PRESTAT',             => 'Prohibited Stat',
        'PRETEMPLATE'           => 'Required Template',
        '!PRETEMPLATE'          => 'Prohibited Template',
        'PRETEXT'               => 'Required Text',
        'PRETYPE'               => 'Required Type',
        '!PRETYPE'              => 'Prohibited Type',
        'PREVAREQ'              => 'Required var. value',
        'PREVARGT'              => 'Var. Must Be Grater',
        'PREVARGTEQ'            => 'Var. Min. Value',
        'PREVARLT'              => 'Var. Must Be Lower',
        'PREVARLTEQ'            => 'Var. Max. Value',
        'PREVARNEQ'             => 'Prohibited Var. Value',
        'PREVISION'             => 'Required Vision',
        '!PREVISION'            => 'Prohibited Vision',
        'PREWEAPONPROF'         => 'Req. Weapond Prof.',
        '!PREWEAPONPROF'        => 'Prohibited Weapond Prof.',
        'PREWIELD'              => 'Required Wield Category',
        '!PREWIELD'             => 'Prohibited Wield Category',
        'PROFICIENCY'           => 'Required Proficiency',
        'PROHIBITED'            => 'Spell Scoll Prohibited',
        'PROHIBITSPELL'         => 'Group of Prohibited Spells',
        'RACESUBTYPE'           => 'Race Subtype',
        'RACETYPE'              => 'Main Race Type',
        'RANGE'                 => 'Range',
        'RATEOFFIRE'            => 'Rate of Fire',
        'REACH'                 => 'Reach',
        'REPEATLEVEL'           => 'Repeat this Level',
        'REMOVABLE'             => 'Removable?',
        'REMOVE'                => 'Remove Object',
        'REP'                   => 'Reputation',
        'SA'                    => 'Special Ability',
        'SA:.CLEAR'             => 'Clear SAs',
        'SAVEINFO'              => 'Save Info',
        'SCHOOL:.CLEAR'         => 'Clear School',
        'SCHOOL'                => 'School',
        'SIZE'                  => 'Size',
        'SKILLLIST'             => 'Use Class Skill List',
        'SOURCE'                => 'Source Index',
        'SOURCEPAGE:.CLEAR'     => 'Clear Source Page',
        'SOURCEPAGE'            => 'Source Page',
        'SOURCELONG'            => 'Source, Long Desc.',
        'SOURCESHORT'           => 'Source, Short Desc.',
        'SOURCEWEB'             => 'Source URI',
        'SPELLBOOK'             => 'Spellbook',
        'SPELLFAILURE'          => '% of Spell Failure',
        'SPELLLIST'             => 'Use Spell List',
        'SPELLLEVEL:CLASS'      => 'List of Class Spells by Level',
        'SPELLLEVEL:DOMAIN'     => 'List of Domain Spells by Level',
        'SPELLRES'              => 'Spell Resistance',
        'SPELL'                 => 'Spells',
        'SPELLS'                => 'Spells',
        'SPELLSTAT'             => 'Spell Stat',
        'SPELLTYPE'             => 'Spell Type',
        'SPROP'                 => 'Special Property',
        'SR'                    => 'Spell Res.',
        'STACK'                 => 'Stackable?',
        'STARTSKILLPTS'         => 'Skill Pts/Lvl',
        'STAT'                  => 'Key Attribute',
        'SUBSCHOOL'             => 'Sub-School',
        'SYNERGY'               => 'Synergy Skill',
        'TARGETAREA'            => 'Target Area',
        'TEMPDESC'              => 'Temporary effect description',
        'TEMPLATE'              => 'Template',
        'TEMPLATE:.CLEAR'       => 'Clear Templates',
        'TYPE'                  => 'Type',
        'TYPE:.CLEAR'           => 'Clear Types',
        'UDAM'                  => 'Unarmed Damage',
        'UMULT'                 => 'Unarmed Multiplier',
        'UNENCUMBEREDMOVE'      => 'Ignore Encumberance',
        'VARIANTS'              => 'Spell Variations',
        'VFEAT'                 => 'Virtual Feat',
        'VISIBLE'               => 'Visible',
        'VISION'                => 'Vision',
        'WEAPONBONUS'           => 'Optionnal Weapon Prof.',
        'WEIGHT'                => 'Weight',
        'WT'                    => 'Weight',
        'XPCOST'                => 'XP Cost',
        'XTRAFEATS'             => 'Starting Feats',
    },

    'BIOSET AGESET' => {
        'AGESET' => '# Age set',
    },

    'BIOSET RACENAME' => {
        'RACENAME' => '# Race name',
    },

    'CLASS' => {
        '000ClassName'  => '# Class Name',
        'ABB'           => 'Abbreviation',
        'ITEMCREATE'    => 'Craft Level Mult.',
        'LEVELSPERFEAT' => 'Levels per Feat',
        'MODTOSKILLS'   => 'Add INT to Skill Points?',
        'MONNONSKILLHD' => 'Extra Hit Die Skills Limit',
        'MULTIPREREQS'  => 'MULTIPREREQS',
        'SPECIALS'      => 'Class Special Ability',
    },

    'CLASS Level' => {
        '000Level', => '# Level',
    },

    'COMPANIONMOD' => {
        '000Follower' => '# Class of the Master',
        'FOLLOWER'    => 'Added Value',
    },

    'DEITY' => {
        '000DeityName'  => '# Deity Name',
        'DOMAINS'       => 'Domains',
        'FOLLOWERALIGN' => 'Clergy AL',
        'DESC'          => 'Desciption of Deity/Title',
        'SYMBOL'        => 'Holy Item',
        'DEITYWEAP'     => 'Deity Weapon',
        'TITLE'         => 'Deity Title',
        'WORSHIPPERS'   => 'Usual Worshippers',
    },

    'EQUIPMENT' => {
        '000EquipmentName' => '# Equipment Name',
        'BASEITEM'         => 'Base Item for EQMOD',
        'SLOTS'            => 'Slot Needed',
        'WIELD'            => 'Wield Category',
    },

    'EQUIPMOD' => {
        '000ModifierName' => '# Modifier Name',
        'ADDPROF'         => 'Add Req. Prof.',
        'ARMORTYPE'       => 'Change Armor Type',
        'ASSIGNTOALL'     => 'Apply to both heads',
        'BONUS:EQM'       => 'BONUS:EQM',
        'BONUS:EQMARMOR'  => 'BONUS:EQMARMOR',
        'BONUS:EQMWEAPON' => 'BONUS:EQMWEAPON',
        'CHARGES'         => 'Nb of Charges',
        'COSTPRE'         => 'Cost before resizing',
        'IGNORES'         => 'Keys to ignore',
        'ITYPE'           => 'Type granted',
        'KEY'             => 'Unique Key',
        'NAMEOPT'         => 'Choose Naming Option',
        'PLUS'            => 'Plus',
        'REPLACES'        => 'Keys to replace',
    },

    'MASTERBONUSRACE' => {
        '000MasterBonusRace' => '# Race of familiar',
    },

    'RACE' => {
        '000RaceName' => '# Race Name',
        'MONCSKILL'   => 'Racial HD Class Skills',
        'MONCCSKILL'  => 'Racial HD Cross-class Skills',
    },

    'SPELL' => {
        '000SpellName' => '# Spell Name',

        'CLASSES' => 'Classes of caster',
        'DOMAINS' => 'Domains granting the spell',
    },

    'SUBCLASS' => {
        '000SubClassName' => '# Subclass',
    },

);

my $tablength = 6;    # Tabulation each 6 characters

my %files_to_parse;   # Will hold the file to parse (including path)
my @lines;            # Will hold all the lines of the file
my @modified_files;   # Will hold the name of the modified files

#####################################
# Verify if the inputpath was given

if ($cl_options{input_path}) {

    # Verify if the outputpath exist
    if ( $cl_options{output_path} && !-d $cl_options{output_path} ) {
        $error_message = "\nThe directory $cl_options{output_path} does not exists.";

        Pod::Usage::pod2usage(
            {   -msg     => $error_message,
                -exitval => 1,
                -output  => \*STDERR,
            }
        );
        exit;
    }

    # Change the \ for / in order to ease things
    $cl_options{input_path}  =~ tr{\\}{/};
    $cl_options{output_path} =~ tr{\\}{/};

    #################################################
    # We populate %valid_tags for all file types.

    for my $line_type ( keys %master_order ) {
        for my $tag ( @{ $master_order{$line_type} } ) {
            if ( $tag =~ / ( .* ) [:][*] \z /xms ) {
                # Tag that end by :* in @master_order are allowed
                # to be present more then once on the same line
                $tag = $1;
                $master_mult{$line_type}{$tag} = 1;
            }

            if ( exists $valid_tags{$line_type}{$tag} ) {
                die "Tag $tag found more then once for $line_type";
            }
            else {
                $valid_tags{$line_type}{$tag} = 1;
            }
        }
    }

    ##########################################################
    # Files that needs to be open for special conversions

    if ( $conversion_enable{'Export lists'} ) {
        # The files should be opened in alpha order since they will
        # be closed in reverse alpha order.

        # Will hold the list of all classes found in CLASS filetypes
        open $filehandle_for{CLASS}, '>', 'class.csv';
        print { $filehandle_for{CLASS} } qq{"Class Name","Line","Filename"\n};

        # Will hold the list of all deities found in DEITY filetypes
        open $filehandle_for{DEITY}, '>', 'deity.csv';
        print { $filehandle_for{DEITY} } qq{"Deity Name","Line","Filename"\n};

        # Will hold the list of all domains found in DOMAIN filetypes
        open $filehandle_for{DOMAIN}, '>', 'domain.csv';
        print { $filehandle_for{DOMAIN} } qq{"Domain Name","Line","Filename"\n};

        # Will hold the list of all equipements found in EQUIPMENT filetypes
        open $filehandle_for{EQUIPMENT}, '>', 'equipment.csv';
        print { $filehandle_for{EQUIPMENT} } qq{"Equipment Name","Output Name","Line","Filename"\n};

        # Will hold the list of all equipmod entries found in EQUIPMOD filetypes
        open $filehandle_for{EQUIPMOD}, '>', 'equipmod.csv';
        print { $filehandle_for{EQUIPMOD} } qq{"Equipmod Name","Key","Type","Line","Filename"\n};

        # Will hold the list of all feats found in FEAT filetypes
        open $filehandle_for{FEAT}, '>', 'feat.csv';
        print { $filehandle_for{FEAT} } qq{"Feat Name","Line","Filename"\n};

        # Will hold the list of all kits found in KIT filetypes
        open $filehandle_for{KIT}, '>', 'kit.csv';
        print { $filehandle_for{KIT} } qq{"Kit Startpack Name","Line","Filename"\n};

        # Will hold the list of all language found in LANGUAGE linetypes
        open $filehandle_for{LANGUAGE}, '>', 'language.csv';
        print { $filehandle_for{LANGUAGE} } qq{"Language Name","Line","Filename"\n};

        # Will hold the list of all PCC files found
        open $filehandle_for{PCC}, '>', 'pcc.csv';
        print { $filehandle_for{PCC} } qq{"SOURCELONG","SOURCESHORT","GAMEMODE","Full Path"\n};

        # Will hold the list of all races and race types found in RACE filetypes
        open $filehandle_for{RACE}, '>', 'race.csv';
        print { $filehandle_for{RACE} } qq{"Race Name","Race Type","Race Subtype","Line","Filename"\n};

        # Will hold the list of all skills found in SKILL filetypes
        open $filehandle_for{SKILL}, '>', 'skill.csv';
        print { $filehandle_for{SKILL} } qq{"Skill Name","Line","Filename"\n};

        # Will hold the list of all spells found in SPELL filetypes
        open $filehandle_for{SPELL}, '>', 'spell.csv';
        print { $filehandle_for{SPELL} } qq{"Spell Name","Source Page","Line","Filename"\n};

        # Will hold the list of all templates found in TEMPLATE filetypes
        open $filehandle_for{TEMPLATE}, '>', 'template.csv';
        print { $filehandle_for{TEMPLATE} } qq{"Tempate Name","Line","Filename"\n};

        # Will hold the list of all variables found in DEFINE tags
        if ( $cl_options{xcheck} ) {
            open $filehandle_for{VARIABLE}, '>', 'variable.csv';
            print { $filehandle_for{VARIABLE} } qq{"Var Name","Line","Filename"\n};
        }

        # We need to list the tags that use Willpower
        if ( $conversion_enable{'ALL:Find Willpower'} ) {
            open $filehandle_for{Willpower}, '>', 'willpower.csv';
            print { $filehandle_for{Willpower} } qq{"Tag","Line","Filename"\n};
        }
    }

    ##########################################################
    # Cross-checking must be activated for the CLASSSPELL
    # convertion to work
    if ( $conversion_enable{'CLASSSPELL convertion to SPELL'} ) {
        $cl_options{xcheck} = 1;
    }

    ##########################################################
    # Parse all the .pcc file to find the other file to parse

    # First, we list the .pcc files in the directory
    my @filelist;
    my %filelist_notpcc;
    my %filelist_missing;

    # Regular expressions for the files that must be skiped by mywanted.
    my @filetoskip = (
        qr(^\.\#),                # Files begining with .# (CVS conflict and deleted files)
        qr(^custom),              # Customxxx files generated by PCGEN
        qr(placeholder\.txt$),    # The CMP directories are full of these
        qr(\.zip$)i,              # Archives present in the directories
        qr(\.rar$)i,
        qr(\.jpg$),               # JPEG files present in the directories

#      qr(readme\.txt$),           # Readme files
#      qr(notes\.txt$),            # Notes files
        qr(\.bak$),               # Backup files

        qr(\.DS_Store$),          # Used with Mac OS
    );

    # Regular expressions for the direcotory that must be skiped by mywanted
    my @dirtoskip = (
        qr(cvs$)i,                # /cvs directories
        qr([.]svn[/])i,           # All .svn directories
        qr([.]svn$)i,             # All .svn directories
        qr(customsources$)i,      # /customsources (for files generated by PCGEN)
        qr(gamemodes)i,           # for the system gameModes directories
#        qr(alpha)i
    );

    sub mywanted {

        # We skip the files from directory matching the REGEX in @dirtoskip
        for my $regex (@dirtoskip) {
            return if $File::Find::dir =~ $regex;
        }

        # We also skip the files that match the REGEX in @filetoskip
        for my $regex (@filetoskip) {
            return if $_ =~ $regex;
        }

        if ( !-d && / [.] pcc \z /xmsi ) {
            push @filelist, $File::Find::name;
        }

        if ( !-d && !/ [.] pcc \z /xmsi ) {
            $filelist_notpcc{$File::Find::name} = lc $_;
        }
    }
    File::Find::find( \&mywanted, $cl_options{input_path} );

    set_ewarn_header("================================================================\n"
                   . "Messages generated while parsing the .PCC files\n"
                   . "----------------------------------------------------------------\n"
    );

    # Second we parse every .PCC and look for filetypes
    for my $pcc_file_name ( sort @filelist ) {
        open my $pcc_fh, '<', $pcc_file_name;

        # Needed to find the full path
        my $currentbasedir = File::Basename::dirname($pcc_file_name);

        my $must_write          = NO;
        my $BOOKTYPE_found      = NO;
        my $GAMEMODE_found      = q{};          # For the PCC export list
        my $SOURCELONG_found    = q{};          #
        my $SOURCESHORT_found   = q{};          #
        my $LST_found           = NO;
        my $CVS_tag_found       = NO;
        my @pcc_lines           = ();
        my %found_filetype;
        my $continue            = YES;

        PCC_LINE:
        while ( <$pcc_fh> ) {
            last PCC_LINE if !$continue;

            chomp;
            $must_write += s/[\x0d\x0a]//g;     # Remove the real and weird CR-LF
            $must_write += s/\s+$//;            # Remove the tralling white spaces

            push @pcc_lines, $_;

            my ( $tag, $value ) = parse_tag( $_, 'PCC', $pcc_file_name, $INPUT_LINE_NUMBER );

            if ( $tag && "$tag:$value" ne $pcc_lines[-1] ) {

                # The parse_tag function modified the values.
                $must_write = YES;
                $pcc_lines[-1] = "$tag:$value";
            }

            if ($tag) {
                if ( $validfiletype{$tag} ) {

                    # Keep track of the filetypes found
                    $found_filetype{$tag}++;

                    $value =~ s/^([^|]*).*/$1/;
                    my $lstfile = find_full_path( $value, $currentbasedir, $cl_options{basepath} );
                    $files_to_parse{$lstfile} = $tag;

                    # Check to see if the file exists
                    if ( !-e $lstfile ) {
                        $filelist_missing{$lstfile} = [ $pcc_file_name, $INPUT_LINE_NUMBER ];
                        delete $files_to_parse{$lstfile};
                    }
                    elsif ($conversion_enable{'SPELL:Add TYPE tags'}
                        && $tag eq 'CLASS' )
                    {

                        # [ 653596 ] Add a TYPE tag for all SPELLs
                        #
                        # The CLASS files must be read before any other
                        $class_files{$lstfile} = 1;
                    }
                    elsif (
                        $tag eq 'SPELL'
                        && (   $conversion_enable{'EQUIPMENT: generate EQMOD'}
                            || $conversion_enable{'CLASS: SPELLLIST from Spell.MOD'} )
                        )
                    {

                        #[ 677962 ] The DMG wands have no charge.
                        #[ 779341 ] Spell Name.MOD to CLASS's SPELLLEVEL
                        #
                        # We keep a list of the SPELL files because they
                        # need to be put in front of the others.

                        $Spell_Files{$lstfile} = 1;
                    }
                    elsif ( $conversion_enable{'CLASSSPELL convertion to SPELL'}
                        && ( $tag eq 'CLASSSPELL' || $tag eq 'CLASS' || $tag eq 'DOMAIN' ) )
                    {

                        # CLASSSPELL convertion
                        # We keep the list of CLASSSPELL, CLASS and DOMAIN
                        # since they must be parse before all the orthers.
                        $classspell_files{$tag}{$lstfile} = 1;

                        # We comment out the CLASSSPELL line
                        if ( $tag eq 'CLASSSPELL' ) {
                            push @pcc_lines, q{#} . pop @pcc_lines;
                            $must_write = YES;

                            ewarn( WARNING,
                                   qq{Commenting out "$pcc_lines[$#pcc_lines]"},
                                   $pcc_file_name,
                                   $INPUT_LINE_NUMBER
                            );
                        }
                    }
                    elsif ($conversion_enable{'CLASSSKILL convertion to CLASS'}
                        && $tag eq 'CLASSSKILL' )
                    {

                        # CLASSSKILL convertion
                        # We keep the list of CLASSSKILL files
                        $classskill_files{$lstfile} = 1;

                        # Make a comment out of the line.
                        push @pcc_lines, q{#} . pop @pcc_lines;
                        $must_write = YES;

                        ewarn( WARNING,
                               qq{Commenting out "$pcc_lines[$#pcc_lines]"},
                               $pcc_file_name,
                               $INPUT_LINE_NUMBER
                        );

                    }

                    #          ($lstfile) = ($lstfile =~ m{/([^/]+)$});
                    delete $filelist_notpcc{$lstfile} if exists $filelist_notpcc{$lstfile};
                    $LST_found = YES;
                }
                elsif ( $tag =~ /^\#/ ) {

                    # It is a comment line
                    $CVS_tag_found = YES if /^\#.*CVS.*Revision/i;
                }
                elsif ( $valid_tags{'PCC'}{$tag} ) {

                    # All the tags that do not have file should be cought here

                    # Get the SOURCExxx tags for future ref.
                    if ($conversion_enable{'SOURCE line replacement'}
                        && (   $tag eq 'SOURCELONG'
                            || $tag eq 'SOURCESHORT'
                            || $tag eq 'SOURCEWEB' )
                        )
                    {
                        my $path = File::Basename::dirname($pcc_file_name);
                        if ( exists $source_tags{$path}{$tag}
                            && $path !~ /custom|altpcc/i )
                        {
                            ewarn( NOTICE,
                                   "$tag already found for $path",
                                   $pcc_file_name,
                                   $INPUT_LINE_NUMBER
                            );
                        }
                        else {
                            $source_tags{$path}{$tag} = "$tag:$value";
                        }

                        # For the PCC report
                        if ( $tag eq 'SOURCELONG' ) {
                            $SOURCELONG_found = $value;
                        }
                        elsif ( $tag eq 'SOURCESHORT' ) {
                            $SOURCESHORT_found = $value;
                        }
                    }
                    elsif ( $tag eq 'GAMEMODE' ) {

                        # Verify that the GAMEMODEs are valid
                        # and match the filer.
                        $GAMEMODE_found = $value;    # The GAMEMODE tag we found
                        my @modes = split /[|]/, $value;
                        my $gamemode_regex =
                            $cl_options{gamemode}
                            ? qr{ \A (?: $cl_options{gamemode} ) \z }xmsi
                            : qr{ . }xms;
                        my $valid_game_mode = $cl_options{gamemode} ? 0 : 1;

                        # First the filter is applied
                        for my $mode (@modes) {
                            if ( $mode =~ $gamemode_regex ) {
                                $valid_game_mode = 1;
                            }
                        }

                        # Then we check if the game mode is valid only if
                        # the game modes have not been filtered out
                        if ($valid_game_mode) {
                            for my $mode (@modes) {
                                if ( !$valid_game_modes{$mode} ) {
                                    ewarn( NOTICE,
                                           qq{Invalid GAMEMODE "$mode" in "$_"},
                                           $pcc_file_name,
                                           $INPUT_LINE_NUMBER
                                    );
                                }
                            }
                        }

                        if ( !$valid_game_mode ) {
                            # We set the variables that will kick us out of the
                            # while loop that read the file and that will
                            # prevent the file from being written.
                            $continue       = NO;
                            $must_write     = NO;
                            $CVS_tag_found  = YES;
                        }
                    }
                    elsif ( $tag eq 'BOOKTYPE' || $tag eq 'TYPE' ) {

                  # Found a TYPE tag
                  #ewarn( NOTICE,  "TYPE should be Publisher.Format.Setting, something is wrong with \"$_\"",
                  #       $pcc_file_name, $INPUT_LINE_NUMBER ) if 2 != tr!.!.!;
                        $BOOKTYPE_found = YES;
                    }
                    elsif ( $tag eq 'GAME' && $conversion_enable{'PCC:GAME to GAMEMODE'} ) {

                        # [ 707325 ] PCC: GAME is now GAMEMODE
                        $pcc_lines[-1] = "GAMEMODE:$value";
                        ewarn( WARNING,
                               qq{Replacing "$tag:$value" by "GAMEMODE:$value"},
                               $pcc_file_name,
                               $INPUT_LINE_NUMBER
                        );
                        $GAMEMODE_found = $value;
                        $must_write     = YES;
                    }
                }
            }
            elsif ( $_ =~ / \A [#] /xms ) {
                # It is a comment line
                 if ( / \A [#] .* CVS .* Revision /xmsi ) {
                    $CVS_tag_found = YES;
                }
            }
            elsif ( / <html> /xmsi ) {
                ewarn( ERROR,
                    "HTML file detected. Maybe you had a problem with your CSV checkout.\n",
                    $pcc_file_name
                );
                $must_write = NO;
                last PCC_LINE;
            }
        }

        close $pcc_fh;

        if ( $conversion_enable{'CLASSSPELL convertion to SPELL'}
          && $found_filetype{'CLASSSPELL'}
          && !$found_filetype{'SPELL'}
        ) {
            ewarn(WARNING,
                  'No SPELL file found, create one.',
                  $pcc_file_name
            );
        }

        if ( $conversion_enable{'CLASSSKILL convertion to CLASS'}
          && $found_filetype{'CLASSSKILL'}
          && !$found_filetype{'CLASS'}
        ) {
            ewarn(WARNING,
                  'No CLASS file found, create one.',
                  $pcc_file_name
            );
        }

        if ( !$BOOKTYPE_found && $LST_found ) {
            ewarn( NOTICE, 'No BOOKTYPE tag found', $pcc_file_name );
        }

        if (!$GAMEMODE_found) {
            ewarn( NOTICE, 'No GAMEMODE tag found', $pcc_file_name );
        }

        if ( $GAMEMODE_found && $cl_options{exportlist} ) {
            print { $filehandle_for{PCC} }
                qq{"$SOURCELONG_found","$SOURCESHORT_found","$GAMEMODE_found","$pcc_file_name"\n};
        }

        # Do we copy the .PCC???
        if ( $cl_options{output_path} && ( $must_write || !$CVS_tag_found ) && $writefiletype{"PCC"} ) {
            my $new_pcc_file = $pcc_file_name;
            $new_pcc_file =~ s/$cl_options{input_path}/$cl_options{output_path}/i;

            # Create the subdirectory if needed
            create_dir( File::Basename::dirname($new_pcc_file), $cl_options{output_path} );

            open my $new_pcc_fh, '>', $new_pcc_file;

            # We keep track of the files we modify
            push @modified_files, $pcc_file_name;

            # We add a CVS revision number is not present
            print {$new_pcc_fh}
                "# CVS \$Revision\$ \$Author\$ -- $today -- reformated by $SCRIPTNAME v$VERSION\n"
                if $pcc_lines[0] !~ / \A [#] .* CVS .* Revision /xmsi;

            for my $line (@pcc_lines) {
                print {$new_pcc_fh} "$line\n";
            }

            close $new_pcc_fh;
        }
    }

    # Is there anything to parse?
    if ( !keys %files_to_parse ) {
        ewarn( ERROR,
            qq{Could not find any .lst file to parse.},
            $cl_options{input_path}
        );
        ewarn( ERROR,
            qq{Is your -inputpath parameter valid? ($cl_options{input_path})},
            $cl_options{input_path}
        );
        if ( $cl_options{gamemode} ) {
            ewarn( ERROR,
                qq{Is your -gamemode parameter valid? ($cl_options{gamemode})},
                $cl_options{input_path}
            );
            exit;
        }
    }

    # Missing .lst files must be printed
    if ( keys %filelist_missing ) {
        set_ewarn_header("================================================================\n"
                       . "List of files used in a .PCC that do not exist\n"
                       . "----------------------------------------------------------------\n"
        );
        for my $lstfile ( sort keys %filelist_missing ) {
            ewarn( NOTICE,
                "Can't find the file: $lstfile",
                $filelist_missing{$lstfile}[0],
                $filelist_missing{$lstfile}[1]
            );
        }
    }

    # If the gamemode filter is active, we do not report file not refered to.
    if ( keys %filelist_notpcc && !$cl_options{gamemode} ) {
        set_ewarn_header("================================================================\n"
                       . "List of files that are not referenced by any .PCC files\n"
                       . "----------------------------------------------------------------\n"
        );

        for my $file ( sort keys %filelist_notpcc ) {
            $file =~ s/$cl_options{basepath}//i;
            $file =~ tr{/}{\\} if $^O eq "MSWin32";
            ewarn( NOTICE,  "$file\n", "" );
        }
    }
}
else {
    $files_to_parse{'STDIN'} = $cl_options{file_type};
}

set_ewarn_header("================================================================\n"
               . "Messages generated while parsing the .LST files\n"
               . "----------------------------------------------------------------\n"
);

my @files_to_parse_sorted = ();
my %temp_files_to_parse   = %files_to_parse;

if ( $conversion_enable{'SPELL:Add TYPE tags'} ) {

    # The CLASS files must be put at the start of the
    # files_to_parse_sorted array in order for them
    # to be dealt with before the SPELL files.

    for my $class_file ( sort keys %class_files ) {
        push @files_to_parse_sorted, $class_file;
        delete $temp_files_to_parse{$class_file};
    }
}

if ( $conversion_enable{'CLASSSPELL convertion to SPELL'} ) {

    # The CLASS and DOMAIN files must be put at the start of the
    # files_to_parse_sorted array in order for them
    # to be dealt with before the CLASSSPELL files.
    # The CLASSSPELL needs to be processed before the SPELL files.

    # CLASS first
    for my $filetype (qw(CLASS DOMAIN CLASSSPELL)) {
        for my $file_name ( sort keys %{ $classspell_files{$filetype} } ) {
            push @files_to_parse_sorted, $file_name;
            delete $temp_files_to_parse{$file_name};
        }
    }
}

if ( keys %Spell_Files ) {

    # The SPELL file must be loaded before the EQUIPMENT
    # in order to properly generate the EQMOD tags or do
    # the Spell.MOD convertion to SPELLLEVEL.

    for my $file_name ( sort keys %Spell_Files ) {
        push @files_to_parse_sorted, $file_name;
        delete $temp_files_to_parse{$file_name};
    }
}

if ( $conversion_enable{'CLASSSKILL convertion to CLASS'} ) {

    # The CLASSSKILL files must be put at the start of the
    # files_to_parse_sorted array in order for them
    # to be dealt with before the CLASS files
    for my $file_name ( sort keys %classskill_files ) {
        push @files_to_parse_sorted, $file_name;
        delete $temp_files_to_parse{$file_name};
    }
}

# We sort the files that need to be parsed.
push @files_to_parse_sorted, sort keys %temp_files_to_parse;

FILE_TO_PARSE:
for my $file (@files_to_parse_sorted) {
    my $numberofcf = 0;    # Number of extra CF found in the file.

    if ( $file eq "STDIN" ) {

        # We read from STDIN
        @lines = <>;
    }
    else {

        # We read only what we know needs to be processed
        next FILE_TO_PARSE if ref( $validfiletype{ $files_to_parse{$file} } ) ne 'CODE';

        # We try to read the file and continue to the next one even if we
        # encounter problems
        eval {
            open my $lst_fh, '<', $file;
            @lines = <$lst_fh>;
            close $lst_fh;
        };

        if ( $EVAL_ERROR ) {
            # There was an error in the eval
            ewarn( ERROR, $EVAL_ERROR, $file );
            next FILE_TO_PARSE;
        }
    }

    # If the file is empty, we skip it
    unless (@lines) {
        ewarn( NOTICE,  "Empty file.", $file );
        next FILE_TO_PARSE;
    }

    # Check to see if we deal with a HTML file
    if ( grep /<html>/i, @lines ) {
        ewarn( ERROR, "HTML file detected. Maybe you had a problem with your CSV checkout.\n", $file );
        next FILE_TO_PARSE;
    }

    # If the first line is the prettylst comment, we remove it.
    my $cvs_line    = "";
    my $cvs_present = 0;
    if ( $lines[0] =~ /\# .* -- reformated by /i || $lines[0] =~ /\#.*CVS.*Revision/i ) {
        $cvs_line    = $lines[0];
        $lines[0]    = '#$$$ CVS comment $$$';
        $cvs_present = 1;
    }
    $cvs_line = ( $cvs_line =~ /(\# cvs.*revision.*author.*?) -- /i )[0]
             || '# CVS $' . 'Revision: $ $' . 'Author: $';

    # Read the full file into the @lines array
    chomp(@lines);

    # Remove and count the abnormal EOL character i.e. anything
    # that reminds after the chomp
    for my $line (@lines) {
        $numberofcf += $line =~ s/[\x0d\x0a]//g;
    }

    if($numberofcf) {
        ewarn( WARNING, "$numberofcf extra CF found and removed.", $file );
    }

    if ( ref( $validfiletype{ $files_to_parse{$file} } ) eq "CODE" ) {

        #    $file_for_error = $file;
        my ($newlines_ref) = &{ $validfiletype{ $files_to_parse{$file} } }(
                                $files_to_parse{$file},
                                \@lines,
                                $file
                             );

        # Let's remove the tralling white spaces
        for my $line (@$newlines_ref) {
            $line =~ s/\s+$//;
        }

        # Some file types are never written
        next FILE_TO_PARSE if !$writefiletype{ $files_to_parse{$file} };

        # We compare the result with the orginal file.
        # If there are no modification, we do not create the new files
        my $same  = NO;
        my $index = 0;

        # First, we check if there are obvious resons not to write the new file
        if (    !$numberofcf                                # No extra CRLF char. were removed
             && $cvs_present                                # CVS head was already present
             && scalar(@lines) == scalar(@$newlines_ref)    # Same number of lines
           ) {
            # We assume the arrays are the same ...
            $same = YES;

            # ... but we check every lines
            $index = -1;
            while ( $same && ++$index < scalar(@lines) ) {
                if ( $lines[$index] ne $newlines_ref->[$index] ) {
                    $same = NO;
                }
            }
        }

        next FILE_TO_PARSE if $same;

        my $write_fh;

        if ($cl_options{output_path}) {
            my $newfile = $file;
            $newfile =~ s/$cl_options{input_path}/$cl_options{output_path}/i;

            # Create the subdirectory if needed
            create_dir( File::Basename::dirname($newfile), $cl_options{output_path} );

            open $write_fh, '>', $newfile;

            # We keep track of the files we modify
            push @modified_files, $file;
        }
        else {
            # Output to standard output
            $write_fh = *STDOUT;
        }

        # The first line of the new file will be a comment line.
        print {$write_fh} "$cvs_line -- $today -- reformated by $SCRIPTNAME v$VERSION\n"
            if $cl_options{output_path} || ( *NEWFILE eq *STDOUT );

        # We print the result
        LINE:
        for my $line ( @{$newlines_ref} ) {
            next LINE if $line eq '#$$$ CVS comment $$$';

            #$line =~ s/\s+$//;
            print {$write_fh} "$line\n" if $cl_options{output_path} || ( *NEWFILE eq *STDOUT );
        }

        close $write_fh if $cl_options{output_path};
    }
    else {
        warn "Didn't process filetype \"$files_to_parse{$file}\".\n";
    }
}

###########################################
# Generate the new BIOSET files

if ( $conversion_enable{'BIOSET:generate the new files'} ) {
    print STDERR "\n================================================================\n";
    print STDERR "List of new BIOSET files generated\n";
    print STDERR "----------------------------------------------------------------\n";

    generate_bioset_files();
}

###########################################
# Print a report with the modified files
if ( $cl_options{output_path} && scalar(@modified_files) ) {
    $cl_options{output_path} =~ tr{/}{\\} if $^O eq "MSWin32";

    set_ewarn_header("================================================================\n"
                   . "List of files that were created in the directory\n"
                   . "$cl_options{output_path}\n"
                   . "----------------------------------------------------------------\n"
    );

    for my $file (@modified_files) {
        $file =~ s{ $cl_options{input_path} }{}xmsi;
        $file =~ tr{/}{\\} if $^O eq "MSWin32";
        ewarn( NOTICE, "$file\n", "" );
    }

    print STDERR "================================================================\n";
}

###########################################
# Print a report for the BONUS and PRExxx usage
if ( $conversion_enable{'Generate BONUS and PRExxx report'} ) {
    $cl_options{output_path} =~ tr{/}{\\} if $^O eq "MSWin32";

    print STDERR "\n================================================================\n";
    print STDERR "List of BONUS and PRExxx tags by linetype\n";
    print STDERR "----------------------------------------------------------------\n";

    my $first = 1;
    for my $line_type ( sort keys %bonus_prexxx_tag_report ) {
        print STDERR "\n" unless $first;
        $first = 0;
        print STDERR "Line Type: $line_type\n";

        for my $tag ( sort keys %{ $bonus_prexxx_tag_report{$line_type} } ) {
            print STDERR "  $tag\n";
        }
    }

    print STDERR "================================================================\n";
}

if ( $cl_options{report} ) {
    ###########################################
    # Print a report for the number of tag
    # found.

    print STDERR "\n================================================================\n";
    print STDERR "Valid tags found\n";
    print STDERR "----------------------------------------------------------------\n";

    my $first = 1;
    REPORT_LINE_TYPE:
    for my $line_type ( sort keys %{ $count_tags{"Valid"} } ) {
        next REPORT_LINE_TYPE if $line_type eq "Total";

        print STDERR "\n" unless $first;
        print STDERR "Line Type: $line_type\n";

        for my $tag ( sort report_tag_sort keys %{ $count_tags{"Valid"}{$line_type} } ) {
            my $tagdisplay = $tag;
            $tagdisplay .= "*" if $master_mult{$line_type}{$tag};
            my $line = "     $tagdisplay";
            $line .= ( " " x ( 26 - length($tagdisplay) ) ) . $count_tags{"Valid"}{$line_type}{$tag};
            print STDERR "$line\n";
        }

        $first = 0;
    }

    print STDERR "\nTotal:\n";

    for my $tag ( sort report_tag_sort keys %{ $count_tags{"Valid"}{"Total"} } ) {
        my $line = "     $tag";
        $line .= ( " " x ( 26 - length($tag) ) ) . $count_tags{"Valid"}{"Total"}{$tag};
        print STDERR "$line\n";
    }
}

if ( exists $count_tags{"Invalid"} ) {

    print STDERR "\n================================================================\n";
    print STDERR "Invalid tags found\n";
    print STDERR "----------------------------------------------------------------\n";

    my $first = 1;
    INVALID_LINE_TYPE:
    for my $linetype ( sort keys %{ $count_tags{"Invalid"} } ) {
        next INVALID_LINE_TYPE if $linetype eq "Total";

        print STDERR "\n" unless $first;
        print STDERR "Line Type: $linetype\n";

        for my $tag ( sort report_tag_sort keys %{ $count_tags{"Invalid"}{$linetype} } ) {

            my $line = "     $tag";
            $line .= ( " " x ( 26 - length($tag) ) ) . $count_tags{"Invalid"}{$linetype}{$tag};
            print STDERR "$line\n";
        }

        $first = 0;
    }

    print STDERR "\nTotal:\n";

    for my $tag ( sort report_tag_sort keys %{ $count_tags{"Invalid"}{"Total"} } ) {
        my $line = "     $tag";
        $line .= ( " " x ( 26 - length($tag) ) ) . $count_tags{"Invalid"}{"Total"}{$tag};
        print STDERR "$line\n";
    }
}

if ( $cl_options{xcheck} ) {

    #####################################################
    # First we process the information that must be added
    # to the %referer and %referer_types;
    for my $parameter_ref (@xcheck_to_process) {
        add_to_xcheck_tables( @{$parameter_ref} );
    }

    #####################################################
    # Print a report with the problems found with xcheck

    my %to_report;

    # Find the entries that need to be reported
    for my $linetype ( sort %referer ) {
        for my $entry ( sort keys %{ $referer{$linetype} } ) {

            # Special case for EQUIPMOD Key
            # -----------------------------
            # If an EQUIPMOD Key entry doesn't exists, we can use the
            # EQUIPMOD name but we have to throw a warning.
            if ( $linetype eq 'EQUIPMOD Key' ) {
                if ( !exists $valid_entities{'EQUIPMOD Key'}{$entry} ) {

                    # There is no key but it might be just a warning
                    if ( exists $valid_entities{'EQUIPMOD'}{$entry} ) {

                        # It's a warning
                        for my $array ( @{ $referer{$linetype}{$entry} } ) {

                            # It's not a warning, not EQUIPMOD were found.
                            push @{ $to_report{ $array->[1] } },
                                [ $array->[2], 'EQUIPMOD Key', $array->[0] ];
                        }
                    }
                    else {
                        for my $array ( @{ $referer{$linetype}{$entry} } ) {

                            # It's not a warning, no EQUIPMOD were found.
                            push @{ $to_report{ $array->[1] } },
                                [ $array->[2], 'EQUIPMOD Key or EQUIPMOD', $array->[0] ];
                        }
                    }
                }
            }
#            elsif (    $linetype eq 'RACE'
#                    && $entry =~ / [%] /xms
#                  ) {
#                # Special PRERACE:xxx% case
#                my $race_text      = $1;
#                my $after_wildcard = $2;
#
#                for my $array ( @{ $referer{$linetype}{$entry} } ) {
#                    if ( $after_wildcard ne q{} ) {
#                        ewarn( NOTICE,
#                               qq{Wildcard context for %, nothing should follow the % in }
#                                    . ,
#
#                    }
#                }
#                ewarn( INFO, q{Can't cross-referencing
#            }
            elsif ( $linetype =~ /,/ ) {

                # Special case if there is a , (coma) in the
                # entry.
                # We must check multiple possible linetypes.
                my $found = 0;

                ITEM:
                for my $item ( split ',', $linetype ) {
                    if ( exists $valid_entities{$item}{$entry} ) {
                        $found = 1;
                        last ITEM;
                    }
                }

                if (!$found) {

                    # Let's have a cute message
                    my @list = split ',', $linetype;
                    my $end_of_message = $list[-2] . ' or ' . $list[-1];
                    pop @list;
                    pop @list;

                    my $message = ( join ', ', @list ) . $end_of_message;

     # push @{$referer{$FileType}{$entry}}, [ $tags{$column}, $file_for_error, $line_for_error ]
                    for my $array ( @{ $referer{$linetype}{$entry} } ) {
                        push @{ $to_report{ $array->[1] } },
                            [ $array->[2], $message, $array->[0] ];
                    }
                }
            }
            else {
                unless ( exists $valid_entities{$linetype}{$entry} ) {

     # push @{$referer{$FileType}{$entry}}, [ $tags{$column}, $file_for_error, $line_for_error ]
                    for my $array ( @{ $referer{$linetype}{$entry} } ) {
                        push @{ $to_report{ $array->[1] } },
                            [ $array->[2], $linetype, $array->[0] ];
                    }
                }
            }
        }
    }

    # Print the report sorted by file name and line number.
    set_ewarn_header("================================================================\n"
                   . "Cross-reference problems found\n"
                   . "----------------------------------------------------------------\n"
    );

    for my $file ( sort keys %to_report ) {
        for my $line_ref ( sort { $a->[0] <=> $b->[0] } @{ $to_report{$file} } ) {
            my $message = qq{No $line_ref->[1] entry for "$line_ref->[2]"};

            # If it is an EQMOD Key missing, it is less severe
            my $message_level = $line_ref->[1] eq 'EQUIPMOD Key' ? INFO : NOTICE;

            ewarn( $message_level,  $message, $file, $line_ref->[0] );
        }
    }

    # Find the type entries that need to be reported
    %to_report = ();
    for my $linetype ( sort %referer_types ) {
        for my $entry ( sort keys %{ $referer_types{$linetype} } ) {
            unless ( exists $valid_types{$linetype}{$entry} ) {
                for my $array ( @{ $referer_types{$linetype}{$entry} } ) {
                    push @{ $to_report{ $array->[1] } }, [ $array->[2], $linetype, $array->[0] ];
                }
            }
        }
    }

    # Print the type report sorted by file name and line number.
    set_ewarn_header("================================================================\n"
                   . "Type cross-reference problems found\n"
                   . "----------------------------------------------------------------\n"
    );

    for my $file ( sort keys %to_report ) {
        for my $line_ref ( sort { $a->[0] <=> $b->[0] } @{ $to_report{$file} } ) {
            ewarn( NOTICE,
                qq{No $line_ref->[1] type found for "$line_ref->[2]"},
                $file,
                $line_ref->[0]
            );
        }
    }

    # Print the tag that do not have defined headers if requested
    if ( $cl_options{missing_header} ) {
        my $firsttime = 1;
        for my $linetype ( sort keys %missing_headers ) {
            if ($firsttime) {
                print STDERR "\n================================================================\n";
                print STDERR "List of TAGs whitout defined header in \%tagheader\n";
                print STDERR "----------------------------------------------------------------\n";
            }

            print STDERR "\n" unless $firsttime;
            print STDERR "Line Type: $linetype\n";

            for my $header ( sort report_tag_sort keys %{ $missing_headers{$linetype} } ) {
                print STDERR "     $header\n";
            }

            $firsttime = 0;
        }
    }

}

#########################################
# Close the files that were opened for
# special convertion

if ( $conversion_enable{'Export lists'} ) {
    # Close all the files in reverse order that they were opened
    for my $line_type ( reverse sort keys %filehandle_for ) {
        close $filehandle_for{$line_type};
    }
}

#########################################
# Close the redirected STDERR if needed

if ($cl_options{output_error}) {
    close STDERR;
}

###############################################################################
###############################################################################
####                                                                       ####
####                      Subroutine Definitions                           ####
####                                                                       ####
###############################################################################
###############################################################################

###############################################################
# FILETYPE_parse
# --------------
#
# This function use the information of master_file_type to
# identify the curent line type and parse it.
#
# Parameters: $file_type      = The type of the file has defined by
#                               the .PCC file
#             $lines_ref      = Reference to an array containing all
#                               the lines of the file
#             $file_for_error = File name to use with ewarn

sub FILETYPE_parse {
    my $file_type      = shift;
    my $lines_ref      = shift;
    my $file_for_error = shift;

    ##################################################
    # Working variables

    my $curent_linetype = "";
    my $last_main_line  = -1;

    my $curent_entity;

    my @newlines;    # New line generated

    ##################################################
    ##################################################
    # Phase I - Split line in tokens and parse
    #           the tokens

    my $line_for_error = 1;
    LINE:
    for my $line (@ {$lines_ref} ) {
        my $line_info;
        my $new_line = $line;    # We work on a copy

        # Remove spaces at the end of the line
        $new_line =~ s/\s+$//;

        # Remove spaces at the begining of the line
        $new_line =~ s/^\s+//;

        # Skip comments and empty lines
        if ( length($new_line) == 0 || $new_line =~ /^\#/ ) {

            # We push the line as is.
            push @newlines,
                [
                $curent_linetype,
                $new_line,
                $last_main_line,
                undef,
                undef,
                ];
            next LINE;
        }

        # Find the line type
        my $index = 0;
        LINE_SPEC:
        for my $line_spec ( @{ $master_file_type{$file_type} } ) {
            if ( $new_line =~ $line_spec->{RegEx} ) {

                # Found it !!!
                $line_info     = $line_spec;
                $curent_entity = $1;
                last LINE_SPEC;
            }
        }
        continue { $index++ }

        # Did we find anything?
        if ( $index >= @{ $master_file_type{$file_type} } ) {
            ewarn(WARNING,
                  qq(Can\'t find the line type for "$new_line"),
                  $file_for_error,
                  $line_for_error
            );

            # We push the line as is.
            push @newlines,
                [
                $curent_linetype,
                $new_line,
                $last_main_line,
                undef,
                undef,
                ];
            next LINE;
        }

        # What type of line is it?
        $curent_linetype = $line_info->{Linetype};
        if ( $line_info->{Mode} == MAIN ) {
            $last_main_line = $line_for_error - 1;
        }
        elsif ( $line_info->{Mode} == SUB ) {
            ewarn( WARNING,
                qq{SUB line "$curent_linetype" is not preceded by a MAIN line},
                $file_for_error,
                $line_for_error
            ) if $last_main_line == -1;
        }
        elsif ( $line_info->{Mode} == SINGLE ) {
            $last_main_line = -1;
        }
        else {
            die qq(Invalide type for $curent_linetype);
        }

        # Split the line in tokens
        my %line_tokens;

        # By default, the tab character is used
        my $sep = $line_info->{SepRegEx} || qr(\t+);

        # We split the tokens, strip the spaces and silently remove the empty tags
        # (empty tokens are the result of [tab][space][tab] type of chracter
        # sequences).
        # [ 975999 ] [tab][space][tab] breaks prettylst
        my @tokens = grep { $_ ne q{} } map { s{ \A \s* | \s* \z }{}xmsg; $_ } split $sep, $new_line;

        #First, we deal with the tag-less columns
        COLUMN:
        for my $column ( @{ $column_with_no_tag{$curent_linetype} } ) {
            last COLUMN if ( scalar @tokens == 0 );

            # We remove the space before and after the token
            #      $tokens[0] =~ s/\s+$//;
            #      $tokens[0] =~ s/^\s+//;

            # We remove the inclosing quotes if any
            ewarn( WARNING,
                qq{Removing quotes around the '$tokens[0]' tag},
                $file_for_error,
                $line_for_error
            ) if $tokens[0] =~ s/^"(.*)"$/$1/;

            my $curent_token = shift @tokens;
            $line_tokens{$column} = [$curent_token];

            # Statistic gathering
            $count_tags{"Valid"}{"Total"}{$column}++;
            $count_tags{"Valid"}{$curent_linetype}{$column}++;

            # Are we dealing with a .MOD, .FORGET or .COPY type of tag?
            if ( index( $column, '000' ) == 0 ) {
                my $check_mod = $line_info->{RegExIsMod} || qr{ \A (.*) [.] (MOD|FORGET|COPY=[^\t]+) }xmsi;

                if ( $line_info->{ValidateKeep} ) {
                    if ( my ($entity_name, $mod_part) = ($curent_token =~ $check_mod) ) {

                        # We keep track of the .MOD type tags to
                        # later validate if they are valid
                        push @{ $referer{$curent_linetype}{$entity_name} },
                            [ $curent_token, $file_for_error, $line_for_error ]
                            if $cl_options{xcheck};

                        # Special case for .COPY=<new name>
                        # <new name> is a valid entity
                        if ( my ($new_name) = ( $mod_part =~ / \A COPY= (.*) /xmsi ) ) {
                            $valid_entities{$curent_linetype}{$new_name}++;
                        }

                        last COLUMN;
                    }
                    else {
                        if ( $cl_options{xcheck} ) {

                            # We keep track of the entities that could be used
                            # with a .MOD type of tag for later validation.
                            #
                            # Some line type need special code to extract the
                            # entry.
                            my $entry = $curent_token;
                            if ( $line_info->{RegExGetEntry} ) {
                                if ( $entry =~ $line_info->{RegExGetEntry} ) {
                                    $entry = $1;

                                    # Some line types refer to other line entries directly
                                    # in the line identifier.
                                    if ( exists $line_info->{GetRefList} ) {
                                        add_to_xcheck_tables(
                                            $line_info->{IdentRefType},
                                            $line_info->{IdentRefTag},
                                            $file_for_error,
                                            $line_for_error,
                                            &{ $line_info->{GetRefList} }($entry)
                                        );
                                    }
                                }
                                else {
                                    ewarn(WARNING,
                                          qq(Cannot find the $curent_linetype name),
                                          $file_for_error,
                                          $line_for_error
                                    );
                                }
                            }

                            $valid_entities{$curent_linetype}{$entry}++;

                            # Check to see if the entry must be recorded for other
                            # entry types.
                            if ( exists $line_info->{OtherValidEntries} ) {
                                for my $entry_type ( @{ $line_info->{OtherValidEntries} } ) {
                                    $valid_entities{$entry_type}{$entry}++;
                                }
                            }
                        }
                    }
                }
            }
        }

        #Second, let's parse the regular columns
        for my $token (@tokens) {
            my $key = parse_tag($token, $curent_linetype, $file_for_error, $line_for_error);

            if ($key) {
                if ( exists $line_tokens{$key} && !exists $master_mult{$curent_linetype}{$key} ) {
                    ewarn( NOTICE,
                        qq{The tag "$key" should not be used more than once on the same $curent_linetype line.\n},
                        $file_for_error,
                        $line_for_error
                    );
                }

                $line_tokens{$key}
                    = exists $line_tokens{$key} ? [ @{ $line_tokens{$key} }, $token ] : [$token];
            }
            else {
                ewarn( WARNING, "No tags in \"$token\"\n", $file_for_error, $line_for_error );
                $line_tokens{$token} = $token;
            }
        }

        my $newline = [
            $curent_linetype,
            \%line_tokens,
            $last_main_line,
            $curent_entity,
            $line_info,
        ];

        ############################################################
        ######################## Conversion ########################
        # We manipulate the tags for the line here
        additionnal_line_parsing(\%line_tokens,
                                 $curent_linetype,
                                 $file_for_error,
                                 $line_for_error,
                                 $newline
        );

        ############################################################
        # Validate the line
        validate_line(\%line_tokens, $curent_linetype, $file_for_error, $line_for_error)
            if $cl_options{xcheck};

        ############################################################
        # .CLEAR order verification
        check_clear_tag_order(\%line_tokens, $file_for_error, $line_for_error);

        #Last, we put the tokens and other line info in the @newlines array
        push @newlines, $newline;

    }
    continue { $line_for_error++ }

    #####################################################
    #####################################################
    # We find all the header lines
    for ( my $line_index = 0; $line_index < @newlines; $line_index++ ) {
        my $curent_linetype = $newlines[$line_index][0];
        my $line_tokens     = $newlines[$line_index][1];
        my $next_linetype;
        $next_linetype = $newlines[ $line_index + 1 ][0]
            if $line_index + 1 < @newlines;

        # A header line either begins with the curent line_type header
        # or the next line header.
        #
        # Only comment -- $line_token is not a hash --  can be header lines
        if ( ref($line_tokens) ne 'HASH' ) {

            # We are on a comment line, we need to find the
            # curent and the next line header.

            # Curent header
            my $this_header =
                $curent_linetype
                ? get_header( $master_order{$curent_linetype}[0], $curent_linetype )
                : "";

            # Next line header
            my $next_header =
                $next_linetype
                ? get_header( $master_order{$next_linetype}[0], $next_linetype )
                : "";

            if (   ( $this_header && index( $line_tokens, $this_header ) == 0 )
                || ( $next_header && index( $line_tokens, $next_header ) == 0 ) )
            {

                # It is a header, let's tag it as such.
                $newlines[$line_index] = [
                    'HEADER',
                    $line_tokens,
                ];
            }
            else {

                # It is just a comment, we won't botter with it ever again.
                $newlines[$line_index] = $line_tokens;
            }
        }
    }

    #my $line_index = 0;
    #for my $line_ref (@newlines)
    #{
    #  my ($curent_linetype, $line_tokens, $main_linetype,
    #      $curent_entity, $line_info) = @$line_ref;
    #
    #  if(ref($line_tokens) ne 'HASH')
    #  {
    #
    #    # Header begins with the line type header.
    #    my $this_header = $curent_linetype
    #                    ? get_header($master_order{$curent_linetype}[0],$file_type)
    #                    : "";
    #    my $next_header = $line_index <= @newlines && ref($newlines[$line_index+1]) eq 'ARRAY' &&
    #                      $newlines[$line_index+1][0]
    #                    ? get_header($master_order{$newlines[$line_index+1][0]}[0],$file_type)
    #                    : "";
    #    if(($this_header && index($line_tokens, $this_header) == 0) ||
    #       ($next_header && index($line_tokens,$next_header) == 0))
    #    {
    #      $line_ref = [ 'HEADER',
    #                    $line_tokens,
    #                  ];
    #    }
    #    else
    #    {
    #      $line_ref = $line_tokens;
    #    }
    #    next;
    #  }
    #
    #} continue { $line_index++ };

    #################################################################
    ######################## Conversion #############################
    # We manipulate the tags for the whole file here

    additionnal_file_parsing(\@newlines, $file_type, $file_for_error);

    ##################################################
    ##################################################
    # Phase II - Reformating the lines

    # No reformating needed?
    return $lines_ref unless $cl_options{output_path} && $writefiletype{$file_type};

    # Now on to all the non header lines.
    CORE_LINE:
    for ( my $line_index = 0; $line_index < @newlines; $line_index++ ) {

        # We skip the text lines and the header lines
        next CORE_LINE
            if ref( $newlines[$line_index] ) ne 'ARRAY'
            || $newlines[$line_index][0] eq 'HEADER';

        my $line_ref = $newlines[$line_index];
        my ($curent_linetype, $line_tokens, $last_main_line,
            $curent_entity,   $line_info
            )
            = @$line_ref;
        my $newline = "";

        # If the separator is not a tab, with just join the
        # tag in order
        my $sep = $line_info->{Sep} || "\t";
        if ( $sep ne "\t" ) {

            # First, the tag known in master_order
            for my $tag ( @{ $master_order{$curent_linetype} } ) {
                if ( exists $line_tokens->{$tag} ) {
                    $newline .= join $sep, @{ $line_tokens->{$tag} };
                    $newline .= $sep;
                    delete $line_tokens->{$tag};
                }
            }

            # The remaining tag are not in the master_order list
            for my $tag ( sort keys %$line_tokens ) {
                $newline .= join $sep, @{ $line_tokens->{$tag} };
                $newline .= $sep;
            }

            # We remove the extra separator
            for ( my $i = 0; $i < length($sep); $i++ ) {
                chop $newline;
            }

            # We replace line_ref with the new line
            $newlines[$line_index] = $newline;
            next CORE_LINE;
        }

        ##################################################
        # The line must be formatted according to its
        # TYPE, FORMAT and HEADER parameters.

        my $mode   = $line_info->{Mode};
        my $format = $line_info->{Format};
        my $header = $line_info->{Header};

        if ( $mode == SINGLE || $format == LINE ) {

            # LINE: the line if formatted independently.
            #       The FORMAT is ignored.
            if ( $header == NO_HEADER ) {

                # Just put the line in order and with a single tab
                # between the columns. If there is a header in the previous
                # line, we remove it.

                # First, the tag known in master_order
                for my $tag ( @{ $master_order{$curent_linetype} } ) {
                    if ( exists $line_tokens->{$tag} ) {
                        $newline .= join $sep, @{ $line_tokens->{$tag} };
                        $newline .= $sep;
                        delete $line_tokens->{$tag};
                    }
                }

                # The remaining tag are not in the master_order list
                for my $tag ( sort keys %$line_tokens ) {
                    $newline .= join $sep, @{ $line_tokens->{$tag} };
                    $newline .= $sep;
                }

                # We remove the extra separator
                for ( my $i = 0; $i < length($sep); $i++ ) {
                    chop $newline;
                }

                # If there was an header before this line, we remove it
                if ( ref( $newlines[ $line_index - 1 ] ) eq 'ARRAY'
                    && $newlines[ $line_index - 1 ][0] eq 'HEADER' )
                {
                    splice( @newlines, $line_index - 1, 1 );
                    $line_index--;
                }

                # Replace the array with the new line
                $newlines[$line_index] = $newline;
                next CORE_LINE;
            }
            elsif ( $header == LINE_HEADER ) {

                # Put the line with a header in front of it.
                my %col_length  = ();
                my $header_line = "";
                my $line_entity = "";

                # Find the length for each column
                $col_length{$_} = mylength( $line_tokens->{$_} ) for ( keys %$line_tokens );

                # Find the columns order and build the header and
                # the curent line
                TAG_NAME:
                for my $tag ( @{ $master_order{$curent_linetype} } ) {

                    # We skip the tag is not present
                    next TAG_NAME if !exists $col_length{$tag};

                    # The first tag is the line entity and most be kept
                    $line_entity = $line_tokens->{$tag}[0] unless $line_entity;

                    # What is the length of the column?
                    my $header_text   = get_header( $tag, $curent_linetype );
                    my $header_length = mylength($header_text);
                    my $col_length    =
                          $header_length > $col_length{$tag}
                        ? $header_length
                        : $col_length{$tag};

                    # Round the col_length up to the next tab
                    $col_length = $tablength * ( int( $col_length / $tablength ) + 1 );

                    # The header
                    my $tab_to_add = int( ( $col_length - $header_length ) / $tablength )
                        + ( ( $col_length - $header_length ) % $tablength ? 1 : 0 );
                    $header_line .= $header_text . $sep x $tab_to_add;

                    # The line
                    $tab_to_add = int( ( $col_length - $col_length{$tag} ) / $tablength )
                        + ( ( $col_length - $col_length{$tag} ) % $tablength ? 1 : 0 );
                    $newline .= join $sep, @{ $line_tokens->{$tag} };
                    $newline .= $sep x $tab_to_add;

                    # Remove the tag we just dealt with
                    delete $line_tokens->{$tag};
                }

                # Add the tags that were not in the master_order
                for my $tag ( sort keys %$line_tokens ) {

                    # What is the length of the column?
                    my $header_text   = get_header( $tag, $curent_linetype );
                    my $header_length = mylength($header_text);
                    my $col_length    =
                          $header_length > $col_length{$tag}
                        ? $header_length
                        : $col_length{$tag};

                    # Round the col_length up to the next tab
                    $col_length = $tablength * ( int( $col_length / $tablength ) + 1 );

                    # The header
                    my $tab_to_add = int( ( $col_length - $header_length ) / $tablength )
                        + ( ( $col_length - $header_length ) % $tablength ? 1 : 0 );
                    $header_line .= $header_text . $sep x $tab_to_add;

                    # The line
                    $tab_to_add = int( ( $col_length - $col_length{$tag} ) / $tablength )
                        + ( ( $col_length - $col_length{$tag} ) % $tablength ? 1 : 0 );
                    $newline .= join $sep, @{ $line_tokens->{$tag} };
                    $newline .= $sep x $tab_to_add;
                }

                # Remove the extra separators (tabs) at the end of both lines
                $header_line =~ s/$sep$//g;
                $newline     =~ s/$sep$//g;

                # Put the header in place
                if ( ref( $newlines[ $line_index - 1 ] ) eq 'ARRAY'
                    && $newlines[ $line_index - 1 ][0] eq 'HEADER' )
                {

                    # We replace the existing header
                    $newlines[ $line_index - 1 ] = $header_line;
                }
                else {

                    # We add the header before the line
                    splice( @newlines, $line_index++, 0, $header_line );
                }

                # Add an empty line in front of the header unless
                # there is already one or the previous line
                # match the line entity.
                if ( $newlines[ $line_index - 2 ] ne ''
                    && index( $newlines[ $line_index - 2 ], $line_entity ) != 0 )
                {
                    splice( @newlines, $line_index - 1, 0, '' );
                    $line_index++;
                }

                # Replace the array with the new line
                $newlines[$line_index] = $newline;
                next CORE_LINE;
            }
            else {

                # Invalid option
                die "Invalid \%master_file_type options: $file_type:$curent_linetype:$mode:$header";
            }
        }
        elsif ( $mode == MAIN ) {
            if ( $format == BLOCK ) {
                #####################################
                # All the main lines must be found
                # up until a different main line type
                # or a ###Block comment.
                my @main_lines;
                my $main_linetype = $curent_linetype;

                BLOCK_LINE:
                for ( my $index = $line_index; $index < @newlines; $index++ ) {

                    # If the line_type  change or
                    # if a '###Block' comment is found,
                    # we are out of the block
                    last BLOCK_LINE
                        if ( ref( $newlines[$index] ) eq 'ARRAY'
                        && ref $newlines[$index][4] eq 'HASH'
                        && $newlines[$index][4]{Mode} == MAIN
                        && $newlines[$index][0] ne $main_linetype )
                        || ( ref( $newlines[$index] ) ne 'ARRAY'
                        && index( lc( $newlines[$index] ), '###block' ) == 0 );

                    # Skip the lines already dealt with
                    next BLOCK_LINE
                        if ref( $newlines[$index] ) ne 'ARRAY'
                        || $newlines[$index][0] eq 'HEADER';

                    push @main_lines, $index
                        if $newlines[$index][4]{Mode} == MAIN;
                }

                #####################################
                # We find the length of each tag for the block
                my %col_length;
                for my $block_line (@main_lines) {
                    for my $tag ( keys %{ $newlines[$block_line][1] } ) {
                        my $col_length = mylength( $newlines[$block_line][1]{$tag} );
                        $col_length{$tag} = $col_length
                            if !exists $col_length{$tag} || $col_length > $col_length{$tag};
                    }
                }

                if ( $header != NO_HEADER ) {

                    # We add the length of the headers if needed.
                    for my $tag ( keys %col_length ) {
                        my $length = mylength( get_header( $tag, $file_type ) );

                        $col_length{$tag} = $length if $length > $col_length{$tag};
                    }
                }

                #####################################
                # Find the columns order
                my %seen;
                my @col_order;

                # First, the columns included in master_order
                for my $tag ( @{ $master_order{$curent_linetype} } ) {
                    push @col_order, $tag if exists $col_length{$tag};
                    $seen{$tag}++;
                }

                # Put the unknown columns at the end
                for my $tag ( sort keys %col_length ) {
                    push @col_order, $tag unless $seen{$tag};
                }

                # Each of the block lines must be reformated
                for my $block_line (@main_lines) {
                    my $newline;

                    for my $tag (@col_order) {
                        my $col_max_length
                            = $tablength * ( int( $col_length{$tag} / $tablength ) + 1 );

                        # Is the tag present in this line?
                        if ( exists $newlines[$block_line][1]{$tag} ) {
                            my $curent_length = mylength( $newlines[$block_line][1]{$tag} );

                            my $tab_to_add
                                = int( ( $col_max_length - $curent_length ) / $tablength )
                                + ( ( $col_max_length - $curent_length ) % $tablength ? 1 : 0 );
                            $newline .= join $sep, @{ $newlines[$block_line][1]{$tag} };
                            $newline .= $sep x $tab_to_add;
                        }
                        else {

                            # We pad with tabs
                            $newline .= $sep x ( $col_max_length / $tablength );
                        }
                    }

                    # We remove the extra $sep at the end
                    $newline =~ s/$sep+$//;

                    # We replace the array with the new line
                    $newlines[$block_line] = $newline;
                }

                if ( $header == NO_HEADER ) {

                    # If there are header before any of the block line,
                    # we need to remove them
                    for my $block_line ( reverse @main_lines ) {
                        if ( ref( $newlines[ $block_line - 1 ] ) eq 'ARRAY'
                            && $newlines[ $block_line - 1 ][0] eq 'HEADER' )
                        {
                            splice( @newlines, $block_line - 1, 1 );
                            $line_index--;
                        }
                    }
                }
                elsif ( $header == LINE_HEADER ) {
                    die "MAIN:BLOCK:LINE_HEADER not implemented yet";
                }
                elsif ( $header == BLOCK_HEADER ) {

                    # We must add the header line at the top of the block
                    # and anywhere else we find them whitin the block.

                    my $header_line;
                    for my $tag (@col_order) {

                        # Round the col_length up to the next tab
                        my $col_max_length
                            = $tablength * ( int( $col_length{$tag} / $tablength ) + 1 );
                        my $curent_header = get_header( $tag, $main_linetype );
                        my $curent_length = mylength($curent_header);
                        my $tab_to_add    = int( ( $col_max_length - $curent_length ) / $tablength )
                            + ( ( $col_max_length - $curent_length ) % $tablength ? 1 : 0 );
                        $header_line .= $curent_header . $sep x $tab_to_add;
                    }

                    # We remove the extra $sep at the end
                    $header_line =~ s/$sep+$//;

                    # Before the top of the block
                    my $need_top_header = NO;
                    if ( ref( $newlines[ $main_lines[0] - 1 ] ) ne 'ARRAY'
                        || $newlines[ $main_lines[0] - 1 ][0] ne 'HEADER' )
                    {
                        $need_top_header = YES;
                    }

                    # Anywhere in the block
                    for my $block_line (@main_lines) {
                        if ( ref( $newlines[ $block_line - 1 ] ) eq 'ARRAY'
                            && $newlines[ $block_line - 1 ][0] eq 'HEADER' )
                        {
                            $newlines[ $block_line - 1 ] = $header_line;
                        }
                    }

                    # Add a header line at the top of the block
                    if ($need_top_header) {
                        splice( @newlines, $main_lines[0], 0, $header_line );
                        $line_index++;
                    }

                }
            }
            else {
                die "Invalid \%master_file_type format: $file_type:$curent_linetype:$mode:$header";
            }
        }
        elsif ( $mode == SUB ) {
            if ( $format == LINE ) {
                die "SUB:LINE not implemented yet";
            }
            elsif ( $format == BLOCK || $format == FIRST_COLUMN ) {
                #####################################
                # Need to find all the file in the SUB BLOCK i.e. same
                # line type within two MAIN lines.
                # If we encounter a ###Block comment, that's the end
                # of the block
                my @sub_lines;
                my $begin_block  = $last_main_line;
                my $sub_linetype = $curent_linetype;

                BLOCK_LINE:
                for ( my $index = $line_index; $index < @newlines; $index++ ) {

                    # If the last_main_line change or
                    # if a '###Block' comment is found,
                    # we are out of the block
                    last BLOCK_LINE
                        if ( ref( $newlines[$index] ) eq 'ARRAY'
                        && $newlines[$index][0] ne 'HEADER'
                        && $newlines[$index][2] != $begin_block )
                        || ( ref( $newlines[$index] ) ne 'ARRAY'
                        && index( lc( $newlines[$index] ), '###block' ) == 0 );

                    # Skip the lines already dealt with
                    next BLOCK_LINE
                        if ref( $newlines[$index] ) ne 'ARRAY'
                        || $newlines[$index][0] eq 'HEADER';

                    push @sub_lines, $index
                        if $newlines[$index][0] eq $curent_linetype;
                }

                #####################################
                # We find the length of each tag for the block
                my %col_length;
                for my $block_line (@sub_lines) {
                    for my $tag ( keys %{ $newlines[$block_line][1] } ) {
                        my $col_length = mylength( $newlines[$block_line][1]{$tag} );
                        $col_length{$tag} = $col_length
                            if !exists $col_length{$tag} || $col_length > $col_length{$tag};
                    }
                }

                if ( $header == BLOCK_HEADER ) {

                    # We add the length of the headers if needed.
                    for my $tag ( keys %col_length ) {
                        my $length = mylength( get_header( $tag, $file_type ) );

                        $col_length{$tag} = $length if $length > $col_length{$tag};
                    }
                }

                #####################################
                # Find the columns order
                my %seen;
                my @col_order;

                # First, the columns included in master_order
                for my $tag ( @{ $master_order{$curent_linetype} } ) {
                    push @col_order, $tag if exists $col_length{$tag};
                    $seen{$tag}++;
                }

                # Put the unknown columns at the end
                for my $tag ( sort keys %col_length ) {
                    push @col_order, $tag unless $seen{$tag};
                }

                # Each of the block lines must be reformated
                if ( $format == BLOCK ) {
                    for my $block_line (@sub_lines) {
                        my $newline;

                        for my $tag (@col_order) {
                            my $col_max_length
                                = $tablength * ( int( $col_length{$tag} / $tablength ) + 1 );

                            # Is the tag present in this line?
                            if ( exists $newlines[$block_line][1]{$tag} ) {
                                my $curent_length = mylength( $newlines[$block_line][1]{$tag} );

                                my $tab_to_add
                                    = int( ( $col_max_length - $curent_length ) / $tablength )
                                    + ( ( $col_max_length - $curent_length ) % $tablength ? 1 : 0 );
                                $newline .= join $sep, @{ $newlines[$block_line][1]{$tag} };
                                $newline .= $sep x $tab_to_add;
                            }
                            else {

                                # We pad with tabs
                                $newline .= $sep x ( $col_max_length / $tablength );
                            }
                        }

                        # We replace the array with the new line
                        $newlines[$block_line] = $newline;
                    }
                }
                else {

                    # $format == FIRST_COLUMN

                    for my $block_line (@sub_lines) {
                        my $newline;
                        my $first_column = YES;
                        my $tab_to_add;

                        TAG:
                        for my $tag (@col_order) {

                            # Is the tag present in this line?
                            next TAG if !exists $newlines[$block_line][1]{$tag};

                            if ($first_column) {
                                my $col_max_length
                                    = $tablength * ( int( $col_length{$tag} / $tablength ) + 1 );
                                my $curent_length = mylength( $newlines[$block_line][1]{$tag} );

                                $tab_to_add
                                    = int( ( $col_max_length - $curent_length ) / $tablength )
                                    + ( ( $col_max_length - $curent_length ) % $tablength ? 1 : 0 );

                                # It's no longer the first column
                                $first_column = NO;
                            }
                            else {
                                $tab_to_add = 1;
                            }

                            $newline .= join $sep, @{ $newlines[$block_line][1]{$tag} };
                            $newline .= $sep x $tab_to_add;
                        }

                        # We replace the array with the new line
                        $newlines[$block_line] = $newline;
                    }
                }

                if ( $header == NO_HEADER ) {

                    # If there are header before any of the block line,
                    # we need to remove them
                    for my $block_line ( reverse @sub_lines ) {
                        if ( ref( $newlines[ $block_line - 1 ] ) eq 'ARRAY'
                            && $newlines[ $block_line - 1 ][0] eq 'HEADER' )
                        {
                            splice( @newlines, $block_line - 1, 1 );
                            $line_index--;
                        }
                    }
                }
                elsif ( $header == LINE_HEADER ) {
                    die "SUB:BLOCK:LINE_HEADER not implemented yet";
                }
                elsif ( $header == BLOCK_HEADER ) {

                    # We must add the header line at the top of the block
                    # and anywhere else we find them whitin the block.

                    my $header_line;
                    for my $tag (@col_order) {

                        # Round the col_length up to the next tab
                        my $col_max_length
                            = $tablength * ( int( $col_length{$tag} / $tablength ) + 1 );
                        my $curent_header = get_header( $tag, $sub_linetype );
                        my $curent_length = mylength($curent_header);
                        my $tab_to_add    = int( ( $col_max_length - $curent_length ) / $tablength )
                            + ( ( $col_max_length - $curent_length ) % $tablength ? 1 : 0 );
                        $header_line .= $header . $sep x $tab_to_add;
                    }

                    # Before the top of the block
                    my $need_top_header = NO;
                    if ( ref( $newlines[ $sub_lines[0] - 1 ] ) ne 'ARRAY'
                        || $newlines[ $sub_lines[0] - 1 ][0] ne 'HEADER' )
                    {
                        $need_top_header = YES;
                    }

                    # Anywhere in the block
                    for my $block_line (@sub_lines) {
                        if ( ref( $newlines[ $block_line - 1 ] ) eq 'ARRAY'
                            && $newlines[ $block_line - 1 ][0] eq 'HEADER' )
                        {
                            $newlines[ $block_line - 1 ] = $header_line;
                        }
                    }

                    # Add a header line at the top of the block
                    if ($need_top_header) {
                        splice( @newlines, $sub_lines[0], 0, $header_line );
                        $line_index++;
                    }

                }
                else {
                    die "Invalid \%master_file_type: $curent_linetype:$mode:$format:$header";
                }
            }
            else {
                die "Invalid \%master_file_type: $curent_linetype:$mode:$format:$header";
            }
        }
        else {
            die "Invalide \%master_file_type mode: $file_type:$curent_linetype:$mode";
        }

    }

    # If there are header lines remaining, we keep the old value
    for (@newlines) {
        $_ = $_->[1] if ref($_) eq 'ARRAY' && $_->[0] eq 'HEADER';
    }

    return \@newlines;

}

###############################################################
# parse_ADD_tag
# -------------
#
# The ADD tag has a very adlib form. It can be many of the
# ADD:Token define in the master_list but is also can be
# of the form ADD:Any test whatsoever(...). And there is also
# the fact that the ':' is used in the name...
#
# In short, it's a pain.
#
# This function return a list of three elements.
#   The first one is a return code
#   The second one is the effective TAG if any
#   The third one is anything found after the tag if any
#
#   Return code 0 = no valid ADD tag found,
#               1 = token ADD tag found,
#               2 = adlib ADD tag found.

sub parse_ADD_tag {
    my $tag = shift;

    if ( $tag =~ /\s*ADD:([^\(|]+)(.*)/ ) {
        my ( $token, $therest ) = ( $1, $2 );

        # Is it a know token?
        if ( exists $token_ADD_tag{"ADD:$token"} ) {
            return ( 1, "ADD:$token", $therest );
        }

        # Is it the right form? => ADD:any text(any text)
        # Note that no check is done to see if the () are balanced.
        elsif ( $therest =~ /^\(.*\)$/ ) {
            return ( 2, "ADD:$token", $therest );
        }
        else {

            # Not a good ADD tag.
            return ( 0, "ADD:$token", $therest );
        }
    }
    else {

        # Not a good ADD tag.
        return ( 0, "", undef );
    }
}

###############################################################
# parse_tag
# ---------
#
# This function
#
# Most commun use is for addition, convertion or removal of tags.
#
# Paramter: $tag_text         Text to parse
#           $linetype         Type for the courent line
#           $file_for_error   Name of the courent file
#           $line_for_error   Number of the courent line
#
# Return:   in scallar context, return $tag
#           in array context, return ($tag,$value)

sub parse_tag {
    my ( $tag_text, $linetype, $file_for_error, $line_for_error ) = @_;
    my $no_more_error = 0;    # Set to 1 if no more error must be displayed.

    # We remove the inclosing quotes if any
    ewarn( WARNING, qq{Removing quotes around the '$tag_text' tag}, $file_for_error, $line_for_error)
        if $tag_text =~ s/^"(.*)"$/$1/;

    # Is this a pragma?
    if ( $tag_text =~ /^(\#.*?):(.*)/ ) {
        return wantarray ? ( $1, $2 ) : $1 if exists $valid_tags{$linetype}{$1};
    }

    # Return already if no text to parse (comment)
    return wantarray ? ( "", "" ) : ""
        if length $tag_text == 0 || $tag_text =~ /^\s*\#/;

    # Remove any spaces before and after the tag
    $tag_text =~ s/^\s+//;
    $tag_text =~ s/\s+$//;

    # Separate the tag name from its value
    my ( $tag, $value ) = split ':', $tag_text, 2;

    # All PCGen should at least have TAG_NAME:TAG_VALUE, anything else
    # is an anomaly. The only exception to this rule is LICENSE that
    # can be used without value to display empty line.
    if ( (!defined $value || $value eq q{})
         && $tag_text ne 'LICENSE:'
       ) {
        ewarn(WARNING,
              qq(The tag "$tag_text" is missing a value (or you forgot a : somewhere)),
              $file_for_error,
              $line_for_error
        );

        # We set the value to prevent further errors
        $value = q{};
    }

    # If there is a ! in front of a PRExxx tag, we remove it
    my $negate_pre = $tag =~ s/^!(pre)/$1/i ? 1 : 0;

    # Special cases like ADD:... and BONUS:...
    if ( $tag eq 'ADD' ) {
        my ( $type, $addtag, $therest ) = parse_ADD_tag( $tag_text );

        if ($type) {

            # It's a ADD:token tag
            if ( $type == 1 ) {
                $tag   = $addtag;
                $value = $therest;
            }
        }
        else {
            unless ( index( $tag_text, '#' ) == 0 ) {
                ewarn( NOTICE,
                    qq{Invalid ADD tag "$tag_text" found in $linetype.},
                    $file_for_error,
                    $line_for_error
                );
                $count_tags{"Invalid"}{"Total"}{$addtag}++;
                $count_tags{"Invalid"}{$linetype}{$addtag}++;
                $no_more_error = 1;
            }
        }
    }

    if ( $tag eq 'BONUS' ) {
        my ($bonus_type) = ( $value =~ /^([^=:|]+)/ );

        if ( $bonus_type && exists $token_BONUS_tag{$bonus_type} ) {

            # Is it valid for the curent file type?
            $tag .= ':' . $bonus_type;
            $value =~ s/^$bonus_type(.*)/$1/;
        }
        elsif ($bonus_type) {

            # No valid bonus type was found
            $count_tags{"Invalid"}{"Total"}{"$tag:$bonus_type"}++;
            $count_tags{"Invalid"}{$linetype}{"$tag:$bonus_type"}++;
            ewarn( NOTICE,
                qq{Invalid BONUS:$bonus_type tag "$tag_text" found in $linetype.},
                $file_for_error,
                $line_for_error
            );
            $no_more_error = 1;
        }
        else {
            $count_tags{"Invalid"}{"Total"}{"BONUS"}++;
            $count_tags{"Invalid"}{$linetype}{"BONUS"}++;
            ewarn( NOTICE,
                qq{Invalid BONUS tag "$tag_text" found in $linetype},
                $file_for_error,
                $line_for_error
            );
            $no_more_error = 1;
        }
    }

    # [ 832171 ] AUTO:* needs to be separate tags
    if ( $tag eq 'AUTO' ) {
        my $found_auto_type;
        AUTO_TYPE:
        for my $auto_type ( sort { length($b) <=> length($a) || $a cmp $b } @token_AUTO_tag ) {
            if ( $value =~ s/^$auto_type// ) {
                # We found what we were looking for
                $found_auto_type = $auto_type;
                last AUTO_TYPE;
            }
        }

        if ($found_auto_type) {
            $tag .= ':' . $found_auto_type;
        }
        else {

            # No valid auto type was found
            if ( $value =~ /^([^=:|]+)/ ) {
                $count_tags{"Invalid"}{"Total"}{"$tag:$1"}++;
                $count_tags{"Invalid"}{$linetype}{"$tag:$1"}++;
                ewarn( NOTICE,
                    qq{Invalid $tag:$1 tag "$tag_text" found in $linetype.},
                    $file_for_error,
                    $line_for_error
                );
            }
            else {
                $count_tags{"Invalid"}{"Total"}{"AUTO"}++;
                $count_tags{"Invalid"}{$linetype}{"AUTO"}++;
                ewarn( NOTICE,
                    qq{Invalid AUTO tag "$tag_text" found in $linetype},
                    $file_for_error,
                    $line_for_error
                );
            }
            $no_more_error = 1;

        }
    }

    # [ 813504 ] SPELLLEVEL:DOMAIN in domains.lst
    # SPELLLEVEL is now a multiple level tag like ADD and BONUS

    if ( $tag eq 'SPELLLEVEL' ) {
        if ( $value =~ s/^CLASS(?=\|)// ) {

            # It's a SPELLLEVEL:CLASS tag
            $tag = "SPELLLEVEL:CLASS";
        }
        elsif ( $value =~ s/^DOMAIN(?=\|)// ) {

            # It's a SPELLLEVEL:DOMAIN tag
            $tag = "SPELLLEVEL:DOMAIN";
        }
        else {

            # No valid SPELLLEVEL subtag was found
            if ( $value =~ /^([^=:|]+)/ ) {
                $count_tags{"Invalid"}{"Total"}{"$tag:$1"}++;
                $count_tags{"Invalid"}{$linetype}{"$tag:$1"}++;
                ewarn( NOTICE,
                    qq{Invalid SPELLLEVEL:$1 tag "$tag_text" found in $linetype.},
                    $file_for_error,
                    $line_for_error
                );
            }
            else {
                $count_tags{"Invalid"}{"Total"}{"SPELLLEVEL"}++;
                $count_tags{"Invalid"}{$linetype}{"SPELLLEVEL"}++;
                ewarn( NOTICE,
                    qq{Invalid SPELLLEVEL tag "$tag_text" found in $linetype},
                    $file_for_error,
                    $line_for_error
                );
            }
            $no_more_error = 1;
        }
    }

    # All the .CLEAR must be separated tags to help with the
    # tag ordering. That is, we need to make sure the .CLEAR
    # is ordered before the normal tag.
    # If the .CLEAR version of the tag doesn't exists, we do not
    # change the tag name but we give a warning.
    #ewarn( DEBUG, qq{parse_tag:$tag_text}, $file_for_error, $line_for_error );
    if ( defined $value && $value =~ /^.CLEAR/i ) {
        if ( !exists $valid_tags{$linetype}{"$tag:.CLEAR"} ) {
            ewarn( NOTICE,
                qq{The tag "$tag:.CLEAR" from "$tag_text" is not in the $linetype tag list\n},
                $file_for_error,
                $line_for_error
            );
            $count_tags{"Invalid"}{"Total"}{"$tag:.CLEAR"}++;
            $count_tags{"Invalid"}{$linetype}{"$tag:.CLEAR"}++;
            $no_more_error = 1;
        }
        else {
            $value =~ s/^.CLEAR//i;
            $tag .= ':.CLEAR';
        }
    }

    # Verify if the tag is valid for the line type
    my $real_tag = ( $negate_pre ? "!" : "" ) . $tag;

    if ( !$no_more_error && !exists $valid_tags{$linetype}{$tag} && index( $tag_text, '#' ) != 0 ) {
        ewarn( NOTICE,
            qq{The tag "$tag" from "$tag_text" is not in the $linetype tag list\n},
            $file_for_error,
            $line_for_error
        );
        $count_tags{"Invalid"}{"Total"}{$real_tag}++;
        $count_tags{"Invalid"}{$linetype}{$real_tag}++;
    }
    elsif ( exists $valid_tags{$linetype}{$tag} ) {

        # Statistic gathering
        $count_tags{"Valid"}{"Total"}{$real_tag}++;
        $count_tags{"Valid"}{$linetype}{$real_tag}++;
    }

    # Check and reformat the values for the tags with
    # only a limited number of values.

    if ( exists $tag_fix_value{$tag} ) {

        # All the limited value are uppercase
        my $newvalue = uc($value);
        my $is_valid = 1;

        # Special treament for the ALIGN tag
        if ( $tag eq 'ALIGN' || $tag eq 'PREALIGN' ) {
            # It is possible for the ALIGN and PREALIGN tags to have more then
            # one value

            # ALIGN use | for separator, PREALIGN use ,
            my $slip_patern = $tag eq 'PREALIGN' ? qr{[,]}xms : qr{[|]}xms;

            for my $align (split $slip_patern, $newvalue) {
                # Is it a number?
                my $number;
                if ( (($number) = ($align =~ / \A (\d+) \z /xms))
                  && $number >= 0
                  && $number < scalar @valid_system_alignments
                ) {
                    $align = $valid_system_alignments[$number];
                    $newvalue =~ s{ (?<! \d ) ($number) (?! \d ) }{$align}xms;
                }

                # Is it a valid alignment?
                if (!exists $tag_fix_value{$tag}{$align}) {
                    ewarn( NOTICE,
                        qq{Invalid value "$align" for tag "$real_tag"},
                        $file_for_error,
                        $line_for_error
                    );
                    $is_valid = 0;
                }
            }
        }
        else {
            # Standerdize the YES NO and other such tags
            if ( exists $tag_proper_value_for{$newvalue} ) {
                $newvalue = $tag_proper_value_for{$newvalue};
            }

            # Is this a proper value for the tag?
            if ( !exists $tag_fix_value{$tag}{$newvalue} ) {
                ewarn( NOTICE,
                    qq{Invalid value "$value" for tag "$real_tag"},
                    $file_for_error,
                    $line_for_error
                );
                $is_valid = 0;
            }
        }



        # Was the tag changed ?
        if ( $is_valid && $value ne $newvalue ) {
            ewarn( WARNING,
                qq{Replaced "$real_tag:$value" by "$real_tag:$newvalue"},
                $file_for_error,
                $line_for_error
            );
            $value = $newvalue;
        }
    }

    ############################################################
    ######################## Conversion ########################
    # We manipulate the tag here
    additionnal_tag_parsing( $real_tag, $value, $linetype, $file_for_error, $line_for_error );

    ############################################################
    # We call the validating function if needed
    validate_tag( $real_tag, $value, $linetype, $file_for_error, $line_for_error )
        if $cl_options{xcheck};

    # If there is already a :  in the tag name, no need to add one more
    my $need_sep = index( $real_tag, ':' ) == -1 ? q{:} : q{};

    ewarn ( DEBUG, qq{parse_tag: $tag_text}, $file_for_error, $line_for_error )
        if $value eq q{};

    # We change the tag_text value from the caller
    # This is very ugly but it gets th job done
    $_[0] = $real_tag;
    $_[0] .= $need_sep . $value if defined $value;

    # Return the tag
    wantarray ? ( $real_tag, $value ) : $real_tag;

}

BEGIN {

    # EQUIPMENT types that are valid in NATURALATTACKS tags
    my %valid_NATURALATTACKS_type = (

        # WEAPONTYPE defined in miscinfo.lst
        Bludgeoning => 1,
        Piercing    => 1,
        Slashing    => 1,
        Fire        => 1,
        Acid        => 1,
        Electricity => 1,
        Cold        => 1,
        Poison      => 1,
        Sonic       => 1,

        # WEAPONCATEGORY defined in miscinfo.lst 3e and 35e
        Simple  => 1,
        Martial => 1,
        Exotic  => 1,
        Natural => 1,

        # Additional WEAPONCATEGORY defined in miscinfo.lst Modern and Sidewinder
        HMG             => 1,
        RocketLauncher  => 1,
        GrenadeLauncher => 1,

        # Additional WEAPONCATEGORY defined in miscinfo.lst Spycraft
        Hurled   => 1,
        Melee    => 1,
        Handgun  => 1,
        Rifle    => 1,
        Tactical => 1,

        # Additional WEAPONCATEGORY defined in miscinfo.lst Xcrawl
        HighTechMartial => 1,
        HighTechSimple  => 1,
        ShipWeapon      => 1,
    );

    my %valid_WIELDCATEGORY = map { $_ => 1 } (

        # From miscinfo.lst 35e
        'Light',
        'OneHanded',
        'TwoHanded',
        'ToSmall',
        'ToLarge',
        'Unusable',
        'None',

        # Hardcoded
        'ALL',
    );

###############################################################
# validate_tag
# ------------
#
# This function store data for later validation. It also check
# the syntax of certain tags and detect commun errors and
# deprecations.
#
# The %referer hash must be populate following this format
# $referer{$lintype}{$name} = [ $err_desc, $file_for_error, $line_for_error ]
#
# Paramter: $tag_name         Name of the tag (before the :)
#           $tag_value        Value of the tag (after the :)
#           $linetype         Type for the courent file
#           $file_for_error   Name of the courent file
#           $line_for_error   Number of the courent line

    sub validate_tag {
        my ( $tag_name, $tag_value, $linetype, $file_for_error, $line_for_error ) = @_;

        # Deprecated tags
        if ( $tag_name eq 'HITDICESIZE' ) {
            ewarn( INFO,
                qq{HITDICESIZE is deprecated, use HITDIE instead.},
                $file_for_error,
                $line_for_error
            );
        }
        elsif ( $tag_name eq 'SPELL' && $linetype ne 'PCC' ) {
            ewarn( INFO,
                qq{SPELL is deprecated, use SPELLS instead.},
                $file_for_error,
                $line_for_error
            );
        }
        elsif ( $tag_name eq 'WEAPONAUTO' ) {
            ewarn( INFO,
                qq{WEAPONAUTO is deprecated, use AUTO:WEAPONPROF instead.},
                $file_for_error,
                $line_for_error
            );
        }

        elsif ( $tag_name =~ /^!?PRE/ ) {

            # It's a PRExxx tag, we delegate
            return validate_pre_tag($tag_name,
                                    $tag_value,
                                    "",
                                    $linetype,
                                    $file_for_error,
                                    $line_for_error
                   );
        }
        elsif ( index( $tag_name, 'BONUS' ) == 0 ) {

            # Are there any PRE tags in the BONUS tag.
            if ( $tag_value =~ /(!?PRE[A-Z]*):([^|]*)/ ) {

                # A PRExxx tag is present
                validate_pre_tag($1,
                                 $2,
                                 "$tag_name$tag_value",
                                 $linetype,
                                 $file_for_error,
                                 $line_for_error
                );
            }

            if ( $tag_name eq 'BONUS:CHECKS' ) {
                # BONUS:CHECKS|<check list>|<jep> {|TYPE=<bonus type>} {|<pre tags>}
                # BONUS:CHECKS|ALL|<jep>          {|TYPE=<bonus type>} {|<pre tags>}
                # <check list> :=   ( <check name 1> { | <check name 2> } { | <check name 3>} )
                #                 | ( BASE.<check name 1> { | BASE.<check name 2> } { | BASE.<check name 3>} )

                # We get parameter 1 and 2 (0 is empty since $tag_value begins with a |)
                my ($check_names,$jep) = ( split /[|]/, $tag_value ) [1,2];

                # The checkname part
                if ( $check_names ne 'ALL' ) {
                    # We skip ALL as it is a special value that must be used alone

                    # $check_name => YES or NO to indicates if BASE. is used
                    my ($found_base, $found_non_base) = ( NO, NO );

                    for my $check_name ( split q{,}, $check_names ) {
                        # We keep the original name for error messages
                        my $clean_check_name = $check_name;

                        # Did we use BASE.? is yes, we remove it
                       if ( $clean_check_name =~ s/ \A BASE [.] //xms ) {
                           $found_base = YES;
                       }
                       else {
                           $found_non_base = YES;
                       }

                        # Is the check name valid
                        if ( !exists $valid_check_name{$clean_check_name} ) {
                            ewarn( NOTICE,
                                qq{Invalid save check name "$clean_check_name" found in "$tag_name$tag_value"},
                                $file_for_error,
                                $line_for_error
                            );
                        }
                    }

                    # Verify if there is a mix of BASE and non BASE
                    if ( $found_base && $found_non_base ) {
                        ewarn( INFO,
                            qq{Are you sure you want to mix BASE and non-BASE in "$tag_name$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }

                # The formula part
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq{@@" in "$tag_name$tag_value},
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            $jep,
                            "$tag_name$tag_value",
                            $file_for_error,
                            $line_for_error
                        )
                     ];

            }
            elsif ( $tag_name eq 'BONUS:FEAT' ) {

                # BONUS:FEAT|POOL|<formula>|<prereq list>|<bonus type>

                # @list_of_param will contains all the non-empty parameters
                # included in $tag_value. The first one should always be
                # POOL.
                my @list_of_param = grep {/./} split '\|', $tag_value;

                if ( ( shift @list_of_param ) ne 'POOL' ) {

                    # For now, only POOL is valid here
                    ewarn( NOTICE,
                        qq{Only POOL is valid as second paramater for BONUS:FEAT "$tag_name$tag_value"},
                        $file_for_error,
                        $line_for_error
                    );
                }

                # The next parameter is the formula
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq(@@" in "$tag_name$tag_value),
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            ( shift @list_of_param ),
                            "$tag_name$tag_value",
                            $file_for_error,
                            $line_for_error
                        )
                     ];

                # For the rest, we need to check if it is a PRExxx tag or a TYPE=
                my $type_present = 0;
                for my $param (@list_of_param) {
                    if ( $param =~ /^(PRE[A-Z]+):(.*)/ ) {

                        # It's a PRExxx tag, we delegate the validation
                        validate_pre_tag($1,
                                         $2,
                                         "$tag_name$tag_value",
                                         $linetype,
                                         $file_for_error,
                                         $line_for_error
                        );
                    }
                    elsif ( $param =~ /^TYPE=(.*)/ ) {
                        $type_present++;
                    }
                    else {
                        ewarn( NOTICE,
                            qq{Invalid parameter "$param" found in "$tag_name$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }

                if ( $type_present > 1 ) {
                    ewarn( NOTICE,
                        qq{There should be only one "TYPE=" in "$tag_name$tag_value"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
            if (   $tag_name eq 'BONUS:MOVEADD'
                || $tag_name eq 'BONUS:MOVEMULT'
                || $tag_name eq 'BONUS:POSTMOVEADD' )
            {

                # BONUS:MOVEMULT|<list of move types>|<number to add or mult>
                # <list of move types> is a coma separated list of a weird TYPE=<move>.
                # The <move> are found in the MOVE tags.
                # <number to add or mult> can be a formula

                my ( $type_list, $formula ) = ( split '\|', $tag_value )[ 1, 2 ];

                # We keep the move types for validation
                for my $type ( split ',', $type_list ) {
                    if ( $type =~ /^TYPE(=|\.)(.*)/ ) {
                        push @xcheck_to_process,
                            [
                            'MOVE Type',     qq(TYPE$1@@" in "$tag_name$tag_value),
                            $file_for_error, $line_for_error,
                            $2
                            ];
                    }
                    else {
                        ewarn( NOTICE,
                            qq(Missing "TYPE=" for "$type" in "$tag_name$tag_value"),
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }

                # Then we deal with the var in formula
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq(@@" in "$tag_name$tag_value),
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            $formula,
                            "$tag_name$tag_value",
                            $file_for_error,
                            $line_for_error
                        )
                     ];
            }
            elsif ( $tag_name eq 'BONUS:SLOTS' ) {

                # BONUS:SLOTS|<slot types>|<number of slots>
                # <slot types> is a coma separated list.
                # The valid types are defined in %token_BONUS_SLOTS_types
                # <number of slots> could be a formula.

                my ( $type_list, $formula ) = ( split '\|', $tag_value )[ 1, 2 ];

                # We first check the slot types
                for my $type ( split ',', $type_list ) {
                    unless ( exists $token_BONUS_SLOTS_types{$type} ) {
                        ewarn( NOTICE,
                            qq{Invalid slot type "$type" in "$tag_name$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }

                # Then we deal with the var in formula
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq(@@" in "$tag_name$tag_value),
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            $formula,
                            "$tag_name$tag_value",
                            $file_for_error,
                            $line_for_error
                        )
                     ];
            }
            elsif ( $tag_name eq 'BONUS:VAR' ) {

               # BONUS:VAR|List of Names|Formula|... only the first two values are variable related.
                my ( $var_name_list, @formulas )
                    = ( split '\|', $tag_value )[ 1, 2 ];

                # First we store the DEFINE variable name
                for my $var_name ( split ',', $var_name_list ) {
                    if ( $var_name =~ /^[a-z][a-z0-9_]*$/i ) {
                        # LIST is filtered out as it may not be valid for the
                        # other places were a variable name is used.
                        if ( $var_name ne 'LIST' ) {
                            push @xcheck_to_process,
                                 [
                                    'DEFINE Variable',
                                    qq(@@" in "$tag_name$tag_value),
                                    $file_for_error,
                                    $line_for_error,
                                    $var_name,
                                 ];
                        }
                    }
                    else {
                        ewarn( NOTICE,
                            qq{Invalid variable name "$var_name" in "$tag_name$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }

                # Second we deal with the formula
                # %CHOICE is filtered out as it may not be valid for the
                # other places were a variable name is used.
                for my $formula ( grep { $_ ne '%CHOICE' } @formulas ) {
                    push @xcheck_to_process,
                         [
                            'DEFINE Variable',
                            qq(@@" in "$tag_name$tag_value),
                            $file_for_error,
                            $line_for_error,
                            parse_jep(
                                $formula,
                                "$tag_name$tag_value",
                                $file_for_error,
                                $line_for_error
                            )
                         ];
                }
            }
            elsif ( $tag_name eq 'BONUS:WIELDCATEGORY' ) {

                # BONUS:WIELDCATEGORY|<List of category>|<formula>
                my ( $category_list, $formula ) = ( split '\|', $tag_value )[ 1, 2 ];

                # Validate the category to see if valid
                for my $category ( split ',', $category_list ) {
                    if ( !exists $valid_WIELDCATEGORY{$category} ) {
                        ewarn( NOTICE,
                            qq{Invalid category "$category" in "$tag_name$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }

                # Second, we deal with the formula
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq(@@" in "$tag_name$tag_value),
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            $formula,
                            "$tag_name$tag_value",
                            $file_for_error,
                            $line_for_error
                        )
                     ];

            }
        }
        elsif ( $tag_name eq 'CLASSES' || $tag_name eq 'DOMAINS' ) {
            if ( $linetype eq 'SPELL' ) {
                my %seen;
                my $tag_to_check = $tag_name eq 'CLASSES' ? 'CLASS' : 'DOMAIN';

                # First we find all the classes used
                for my $level ( split '\|', $tag_value ) {
                    if ( $level =~ /(.*)=(\d+)/ ) {
                        for my $entity ( split ',', $1 ) {

                            # [ 849365 ] CLASSES:ALL
                            # CLASSES:ALL is OK
                            # Arcane and Divine are not really OK but there are used
                            # as placeholder for in the MSRD.
                            if ((  $tag_to_check eq "CLASS"
                                   && (   $entity ne "ALL"
                                       && $entity ne "Arcane"
                                       && $entity ne "Divine" )
                                )
                                || $tag_to_check eq "DOMAIN"
                                )
                            {
                                push @xcheck_to_process,
                                    [
                                    $tag_to_check,   $tag_name,
                                    $file_for_error, $line_for_error,
                                    $entity
                                    ];

                                if ( $seen{$entity}++ ) {
                                    ewarn( NOTICE,
                                        qq{"$entity" found more then once in $tag_name},
                                        $file_for_error,
                                        $line_for_error
                                    );
                                }
                            }
                        }
                    }
                    else {
                        ewarn( WARNING,
                            qq{Missing "=level" after "$tag_name:$level"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }
            }
            elsif ( $linetype eq 'SKILL' ) {

                # Only CLASSES in SKILL
                CLASS_FOR_SKILL:
                for my $class ( split '\|', $tag_value ) {

                    # ALL is valid here
                    next CLASS_FOR_SKILL if $class eq 'ALL';

                    push @xcheck_to_process,
                        [
                        'CLASS',         $tag_name,
                        $file_for_error, $line_for_error,
                        $class
                        ];
                }
            }
            elsif (   $linetype eq 'DEITY' ) {
                # Only DOMAINS in DEITY
                DOMAIN_FOR_DEITY:
                for my $domain ( split ',', $tag_value ) {

                    # ALL is valid here
                    next DOMAIN_FOR_DEITY if $domain eq 'ALL';

                    push @xcheck_to_process,
                        [
                        'DOMAIN',        $tag_name,
                        $file_for_error, $line_for_error,
                        $domain
                        ];
                }
            }
        }
        elsif ( $tag_name eq 'CLASS'
             && $linetype ne 'PCC'
        ) {
            # Note: The CLASS linetype doesn't have any CLASS tag, it's
            #       called 000ClassName internaly. CLASS is a tag used
            #       in other line types like KIT CLASS.
            # CLASS:<class name>,<class name>,...[BASEAGEADD:<dice expression>]

            # We remove and ignore [BASEAGEADD:xxx] if present
            my $list_of_class = $tag_value;
            $list_of_class =~ s{ \[ BASEAGEADD: [^]]* \] }{}xmsg;

            push @xcheck_to_process,
                 [
                    'CLASS',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    (split /[|,]/, $list_of_class),
                 ];
        }
        elsif ( $tag_name eq 'DEITY'
             && $linetype ne 'PCC'
        ) {
            # DEITY:<deity name>|<deity name>|etc.
            push @xcheck_to_process,
                 [
                    'DEITY',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    (split /[|]/, $tag_value),
                 ];
        }
        elsif ( $tag_name eq 'DOMAIN'
             && $linetype ne 'PCC'
        ) {
            # DOMAIN:<domain name>|<domain name>|etc.
            push @xcheck_to_process,
                 [
                    'DOMAIN',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    (split /[|]/, $tag_value),
                 ];
        }
        elsif ( $tag_name eq 'ADDDOMAINS' ) {

            # ADDDOMAINS:<domain1>.<domain2>.<domain3>. etc.
            push @xcheck_to_process,
                [
                'DOMAIN',        $tag_name,
                $file_for_error, $line_for_error,
                split '\.',      $tag_value
                ];
        }
        elsif ( $tag_name eq 'ADD:SPELLCASTER' ) {

            # ADD:SPELLCASTER(<list of classes>)<formula>
            if ( $tag_value =~ /\((.*)\)(.*)/ ) {
                my ( $list, $formula ) = ( $1, $2 );

                # First the list of classes
                # ANY, ARCANA, DIVINE and PSIONIC are spcial hardcoded cases for
                # the ADD:SPELLCASTER tag.
                push @xcheck_to_process, [
                    'CLASS',
                    qq(@@" in "$tag_name$tag_value),
                    $file_for_error,
                    $line_for_error,
                    grep {
                               uc($_) ne 'ANY'
                            && uc($_) ne 'ARCANE'
                            && uc($_) ne 'DIVINE'
                            && uc($_) ne 'PSIONIC'
                        }
                        split ',', $list
                ];

                # Second, we deal with the formula
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq(@@" from "$formula" in "$tag_name$tag_value),
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            $formula,
                            "$tag_name$tag_value",
                            $file_for_error,
                            $line_for_error
                        )
                     ];
            }
            else {
                ewarn( NOTICE,
                    qq{Invalid syntax: "$tag_name$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif ( $tag_name eq 'ADD:EQUIP' ) {

            # ADD:EQUIP(<list of equipments>)<formula>
            if ( $tag_value =~ m{ [(]   # Opening brace
                                  (.*)  # Everything between braces include other braces
                                  [)]   # Closing brase
                                  (.*)  # The rest
                               }xms ) {
                my ( $list, $formula ) = ( $1, $2 );

                # First the list of equipements
                # ANY is a spcial hardcoded cases for ADD:EQUIP
                push @xcheck_to_process,
                    [
                        'EQUIPMENT',
                        qq(@@" in "$tag_name$tag_value),
                        $file_for_error,
                        $line_for_error,
                        grep { uc($_) ne 'ANY' }
                            split ',', $list
                    ];

                # Second, we deal with the formula
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq(@@" from "$formula" in "$tag_name$tag_value),
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            $formula,
                            "$tag_name$tag_value",
                            $file_for_error,
                            $line_for_error
                        )
                     ];
            }
            else {
                ewarn( NOTICE,
                    qq{Invalid syntax: "$tag_name$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif ($tag_name eq 'EQMOD'
            || $tag_name eq 'IGNORES'
            || $tag_name eq 'REPLACES'
            || ( $tag_name =~ /!?PRETYPE/ && $tag_value =~ /(\d+,)?EQMOD=/ )
        ) {

            # This section check for any reference to an EQUIPMOD key
            if ( $tag_name eq 'EQMOD' ) {

                # The higher level for the EQMOD is the . (who's the genius who
                # dreamed that up...
                my @key_list = split '\.', $tag_value;

                # The key name is everything found before the first |
                for $_ (@key_list) {
                    my ($key) = (/^([^|]*)/);
                    if ($key) {

                        # To be processed later
                        push @xcheck_to_process,
                            [
                            'EQUIPMOD Key',  qq(@@" in "$tag_name:$tag_value),
                            $file_for_error, $line_for_error,
                            $key
                            ];
                    }
                    else {
                        ewarn(WARNING,
                              qq(Cannot find the key for "$_" in "$tag_name:$tag_value"),
                              $file_for_error,
                              $line_for_error
                        );
                    }
                }
            }
            elsif ( $tag_name eq "IGNORES" || $tag_name eq "REPLACES" ) {

                # Coma separated list of KEYs
                # To be processed later
                push @xcheck_to_process,
                    [
                    'EQUIPMOD Key',  qq(@@" in "$tag_name:$tag_value),
                    $file_for_error, $line_for_error,
                    split ',',       $tag_value
                    ];
            }
        }
        elsif (
            $linetype ne 'PCC'
            && (   $tag_name eq 'ADD:FEAT'
                || $tag_name eq 'AUTO:FEAT'
                || $tag_name eq 'FEAT'
                || $tag_name eq 'FEATAUTO'
                || $tag_name eq 'VFEAT'
                || $tag_name eq 'MFEAT' )
            )
        {
            my @feats;
            my $parent = NO;

            # ADD:FEAT(feat,feat,TYPE=type)formula
            # FEAT:feat|feat|feat(xxx)
            # FEAT:feat,feat,feat(xxx)  in the TEMPLATE and DOMAIN
            # FEATAUTO:feat|feat|...
            # VFEAT:feat|feat|feat(xxx)|PRExxx:yyy
            # MFEAT:feat|feat|feat(xxx)|...
            # All these type may have embeded [PRExxx tags]
            if ( $tag_name eq 'ADD:FEAT' ) {
                if ( $tag_value =~ /^\((.*)\)(.*)?$/ ) {
                    $parent = YES;
                    my $formula = $2;

                    # The ADD:FEAT list may contains list elements that
                    # have () and will need the special split.
                    # The LIST special feat name is valid in ADD:FEAT
                    @feats = grep { $_ ne 'LIST' } embedded_coma_split($1);

                    #        # We put the , back in place
                    #        s/&coma;/,/g for @feats;

                    # Here we deal with the formula part
                    push @xcheck_to_process,
                         [
                            'DEFINE Variable',
                            qq(@@" in "$tag_name$tag_value),
                            $file_for_error,
                            $line_for_error,
                            parse_jep(
                                $formula,
                                "$tag_name$tag_value",
                                $file_for_error,
                                $line_for_error
                            )
                         ] if $formula;
                }
                else {
                    ewarn( NOTICE,
                        qq{Invilid systax: "$tag_name$tag_value"},
                        $file_for_error,
                        $line_for_error
                    ) if $tag_value;
                }
            }
            elsif ( $tag_name eq 'FEAT' ) {

                # FEAT tags sometime use , and sometime use | as separator.

                # We can now safely split on the ,
                @feats = embedded_coma_split( $tag_value, qr{,|\|} );

                #      # We put the , back in place
                #      s/&coma;/,/g for @feats;
            }
            else {
                @feats = split '\|', $tag_value;
            }

            FEAT:
            for my $feat (@feats) {

                # If it is a PRExxx tag section, we validate teh PRExxx tag.
                if ( $tag_name eq 'VFEAT' && $feat =~ /^(!?PRE[A-Z]+):(.*)/ ) {
                    validate_pre_tag($1,
                                     $2,
                                     "$tag_name:$tag_value",
                                     $linetype,
                                     $file_for_error,
                                     $line_for_error
                    );
                    $feat = "";
                    next FEAT;
                }

                # We strip the embeded [PRExxx ...] tags
                if ( $feat =~ /([^[]+)\[(!?PRE[A-Z]*):(.*)\]$/ ) {
                    $feat = $1;
                    validate_pre_tag($2,
                                     $3,
                                     "$tag_name:$tag_value",
                                     $linetype,
                                     $file_for_error,
                                     $line_for_error
                    );
                }

            }

            my $message_format = $tag_name;
            if ($parent) {
                $message_format = "$tag_name(@@)";
            }

            # To be processed later
            push @xcheck_to_process,
                [ 'FEAT', $message_format, $file_for_error, $line_for_error, @feats ];
        }
        elsif ( $tag_name eq 'KIT' && $linetype ne 'PCC' ) {
            # KIT:<number of choice>|<kit name>|<kit name>|etc.
            # KIT:<kit name>
            my @kit_list = split /[|]/, $tag_value;

            # The first item might be a number
            if ( $kit_list[0] =~ / \A \d+ \z /xms ) {
                # We discard the number
                shift @kit_list;
            }

            push @xcheck_to_process,
                 [
                    'KIT STARTPACK',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    @kit_list,
                 ];
        }
        elsif ( $tag_name eq 'LANGAUTOxxx' || $tag_name eq 'LANGBONUS' ) {

            # To be processed later
            # The ALL keyword is removed here since it is not usable everywhere there are language
            # used.
            push @xcheck_to_process,
                [
                'LANGUAGE', $tag_name, $file_for_error, $line_for_error,
                grep { $_ ne 'ALL' } split ',', $tag_value
                ];
        }
        elsif ( $tag_name eq 'ADD:Language' ) {

            # Syntax: ADD:LANGUAGE(<coma separated list of languages)<number>
            if ( $tag_value =~ /\((.*)\)/ ) {
                push @xcheck_to_process,
                    [
                    'LANGUAGE', 'ADD:Language(@@)', $file_for_error, $line_for_error,
                    split ',',  $1
                    ];
            }
            else {
                ewarn( NOTICE,
                    qq{Invalid syntax for "$tag_name$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif ( $tag_name eq 'MOVE' ) {

            # MOVE:<move type>,<value>
            # ex. MOVE:Walk,30,Fly,20,Climb,10,Swim,10

            my @list = split ',', $tag_value;

            MOVE_PAIR:
            while (@list) {
                my ( $type, $value ) = ( splice @list, 0, 2 );
                $value = "" if !defined $value;

                # $type should be a word and $value should be a number
                if ( $type =~ /^\d+$/ ) {
                    ewarn( NOTICE,
                        qq{I was expecting a move type where I found "$type" in "$tag_name:$tag_value"},
                        $file_for_error,
                        $line_for_error
                    );
                    last;
                }
                else {

                    # We keep the move type for future validation
                    $valid_entities{'MOVE Type'}{$type}++;
                }

                unless ( $value =~ /^\d+$/ ) {
                    ewarn( NOTICE,
                        qq{I was expecting a number after "$type" and found "$value" in "$tag_name:$tag_value"},
                        $file_for_error,
                        $line_for_error
                    );
                    last MOVE_PAIR;
                }
            }
        }
        elsif ( $tag_name eq 'RACE' && $linetype ne 'PCC' ) {
            # There is only one race per RACE tag
            push @xcheck_to_process,
                 [  'RACE',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    $tag_value,
                 ];
        }
        elsif ( $tag_name eq 'SWITCHRACE' ) {

            # To be processed later
            # Note: SWITCHRACE actually switch the race TYPE
            push @xcheck_to_process,
                [   'RACE TYPE',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    (split '\|',  $tag_value),
                ];
        }
        elsif ( $tag_name eq 'CSKILL'
             || $tag_name eq 'CCSKILL'
             || $tag_name eq 'MONCSKILL'
             || $tag_name eq 'MONCCSKILL'
             || ($tag_name eq 'SKILL' && $linetype ne 'PCC')
        ) {
            my @skills = split /[|]/, $tag_value;

            # We need to filter out %CHOICE for the SKILL tag
            if ( $tag_name eq 'SKILL' ) {
                @skills = grep { $_ ne '%CHOICE' } @skills;
            }

            # To be processed later
            push @xcheck_to_process,
                [   'SKILL',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    @skills,
                ];
        }
        elsif ( $tag_name eq 'ADD:SKILL' ) {

            # ADD:SKILL(<list of skills>)<formula>
            if ( $tag_value =~ /\((.*)\)(.*)/ ) {
                my ( $list, $formula ) = ( $1, $2 );

                # First the list of skills
                # ANY is a spcial hardcoded cases for ADD:EQUIP
                push @xcheck_to_process,
                     [
                       'SKILL',
                       qq(@@" in "$tag_name$tag_value),
                       $file_for_error,
                       $line_for_error,
                       grep { uc($_) ne 'ANY' } split ',', $list
                     ];

                # Second, we deal with the formula
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq(@@" from "$formula" in "$tag_name$tag_value),
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            $formula,
                            "$tag_name$tag_value",
                            $file_for_error,
                            $line_for_error
                        ),
                     ];
            }
            else {
                ewarn( NOTICE,
                    qq{Invalid syntax: "$tag_name$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif ( $tag_name eq 'SPELLS' ) {
            if ( $linetype ne 'KIT SPELLS' ) {
 # Syntax: SPELLS:<spellbook>|[TIMES=<time per day>|][CASTERLEVEL=<CL>|]<Spell list>[|<prexxx tags>]
 # <Spell list> = <Spell name>,<DC> [|<Spell list>]
                my @list_of_param = split '\|', $tag_value;
                my @spells;

                # We drop the Spell book name
                shift @list_of_param;

                my $nb_times       = 0;
                my $nb_casterlevel = 0;
                for my $param (@list_of_param) {
                    if ( $param =~ /^(TIMES)=(.*)/ || $param =~ /^(CASTERLEVEL)=(.*)/ ) {

                        # The formulas need to be validated
                        push @xcheck_to_process,
                             [
                                'DEFINE Variable',
                                qq(@@" in "$tag_name:$tag_value),
                                $file_for_error,
                                $line_for_error,
                                parse_jep(
                                    $2,
                                    "$tag_name:$tag_value",
                                    $file_for_error,
                                    $line_for_error
                            )
                             ];
                        if ( $1 eq 'TIMES' ) {
                            $nb_times++;
                        }
                        else {
                            $nb_casterlevel++;
                        }
                    }
                    elsif ( $param =~ /^(PRE[A-Z]+):(.*)/ ) {

                        # Embeded PRExxx tags
                        validate_pre_tag($1,
                                         $2,
                                         "$tag_name:$tag_value",
                                         $linetype,
                                         $file_for_error,
                                         $line_for_error
                        );
                    }
                    else {
                        my ( $spellname, $dc ) = ( $param =~ /([^,]+),(.*)/ );

                        if ($dc) {

                            # Spell name must be validated with the list of spells and DC is a formula
                            push @spells, $spellname;

                            push @xcheck_to_process,
                                 [
                                    'DEFINE Variable',
                                    qq(@@" in "$tag_name:$tag_value),
                                    $file_for_error,
                                    $line_for_error,
                                    parse_jep(
                                        $dc,
                                        "$tag_name:$tag_value",
                                        $file_for_error,
                                        $line_for_error
                                    )
                                 ];
                        }
                        else {

                            # No DC present, the whole param is the spell name
                            push @spells, $param;

                            ewarn(INFO,
                                  qq(the DC value is missing for "$param" in "$tag_name:$tag_value"),
                                  $file_for_error,
                                  $line_for_error
                            );
                        }
                    }
                }

                push @xcheck_to_process,
                    [
                    'SPELL',         $tag_name,
                    $file_for_error, $line_for_error,
                    @spells
                    ];

                # Validate the number of TIMES and CASTERLEVEL parameters
                if ( $nb_times != 1 ) {
                    if ($nb_times) {
                        ewarn( NOTICE,
                            qq{TIMES= should not be used more then once in "$tag_name:$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                    else {
                        ewarn( INFO,
                            qq(the TIMES= parameter is missing in "$tag_name:$tag_value"),
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }

                if ( $nb_casterlevel != 1 ) {
                    if ($nb_casterlevel) {
                        ewarn( NOTICE,
                            qq{CASTERLEVEL= should not be used more then once in "$tag_name:$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                    else {
                        ewarn( INFO,
                            qq(the CASTERLEVEL= parameter is missing in "$tag_name:$tag_value"),
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }
            }
            else {
                # KIT SPELLS line type
                # SPELLS:<parameter list>|<spell list>
                # <parameter list> = <param id> = <param value { | <parameter list> }
                # <spell list> := <spell name> { = <number> } { | <spell list> }
                my @spells = ();

                for my $spell_or_param (split q{\|}, $tag_value) {
                    # Is it a parameter?
                    if ( $spell_or_param =~ / \A ([^=]*) = (.*) \z/xms ) {
                        my ($param_id,$param_value) = ($1,$2);

                        if ( $param_id eq 'CLASS' ) {
                            push @xcheck_to_process,
                                 [
                                    'CLASS',
                                    qq{@@" in "$tag_name:$tag_value},
                                    $file_for_error,
                                    $line_for_error,
                                    $param_value,
                                 ];

                        }
                        elsif ( $param_id eq 'SPELLBOOK') {
                            # Nothing to do
                        }
                        elsif ( $param_value =~ / \A \d+ \z/mxs ) {
                            # It's a spell after all...
                            push @spells, $param_id;
                        }
                        else {
                            ewarn( NOTICE,
                                qq{Invalide SPELLS parameter: "$spell_or_param" found in "$tag_name:$tag_value"},
                                $file_for_error,
                                $line_for_error
                            );
                        }
                    }
                    else {
                        # It's a spell
                        push @spells, $spell_or_param;
                    }
                }

                if ( scalar @spells ) {
                    push @xcheck_to_process,
                         [
                            'SPELL',
                            $tag_name,
                            $file_for_error,
                            $line_for_error,
                            @spells,
                         ];
                }
            }
        }
        elsif ( index( $tag_name, 'SPELLLEVEL:' ) == 0 ) {

            # [ 813504 ] SPELLLEVEL:DOMAIN in domains.lst
            # -------------------------------------------
            # There are two different SPELLLEVEL tags that must
            # be x-check. SPELLLEVEL:CLASS and SPELLLEVEL:DOMAIN.
            #
            # The CLASS type have CLASSes and SPELLs to check and
            # the DOMAIN type have DOMAINs and SPELLs to check.

            if ( $tag_name eq "SPELLLEVEL:CLASS" ) {

                # The syntax for SPELLLEVEL:CLASS is
                # SPELLLEVEL:CLASS|<class-list of spells>
                # <class-list of spells> := <class> | <list of spells> [ | <class-list of spells> ]
                # <class>                := <class name> = <level>
                # <list of spells>       := <spell name> [, <list of spells>]
                # <class name>           := ASCII WORDS that must be validated
                # <level>                := INTEGER
                # <spell name>           := ASCII WORDS that must be validated
                #
                # ex. SPELLLEVEL:CLASS|Wizard=0|Detect Magic,Read Magic|Wizard=1|Burning Hands

                # We extract the classes and the spell names
                if ( my $working_value = $tag_value ) {
                    while ($working_value) {
                        if ( $working_value =~ s/\|([^|]+)\|([^|]+)// ) {
                            my $class  = $1;
                            my $spells = $2;

                            # The CLASS
                            if ( $class =~ /([^=]+)\=(\d+)/ ) {

                                # [ 849369 ] SPELLCASTER.Arcane=1
                                # SPELLCASTER.Arcane and SPELLCASTER.Divine are specials
                                # CLASS names that should not be cross-referenced.
                                # To be processed later
                                push @xcheck_to_process,
                                    [
                                    'CLASS', qq(@@" in "$tag_name$tag_value),
                                    $file_for_error, $line_for_error, $1
                                    ];
                            }
                            else {
                                ewarn( NOTICE,
                                    qq{Invalid syntax for "$class" in "$tag_name$tag_value"},
                                    $file_for_error,
                                    $line_for_error
                                );
                            }

                            # The SPELL names
                            # To be processed later
                            push @xcheck_to_process,
                                [
                                'SPELL',         qq(@@" in "$tag_name$tag_value),
                                $file_for_error, $line_for_error,
                                split ',',       $spells
                                ];
                        }
                        else {
                            ewarn( NOTICE,
                                qq{Invalid class/spell list paring in "$tag_name$tag_value"},
                                $file_for_error,
                                $line_for_error
                            );
                            $working_value = "";
                        }
                    }
                }
                else {
                    ewarn( NOTICE,
                        qq{No value found for "$tag_name"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
            if ( $tag_name eq "SPELLLEVEL:DOMAIN" ) {

              # The syntax for SPELLLEVEL:DOMAIN is
              # SPELLLEVEL:CLASS|<domain-list of spells>
              # <domain-list of spells> := <domain> | <list of spells> [ | <domain-list of spells> ]
              # <domain>                := <domain name> = <level>
              # <list of spells>        := <spell name> [, <list of spells>]
              # <domain name>           := ASCII WORDS that must be validated
              # <level>                 := INTEGER
              # <spell name>            := ASCII WORDS that must be validated
              #
              # ex. SPELLLEVEL:DOMAIN|Air=1|Obscuring Mist|Animal=4|Repel Vermin

                # We extract the classes and the spell names
                if ( my $working_value = $tag_value ) {
                    while ($working_value) {
                        if ( $working_value =~ s/\|([^|]+)\|([^|]+)// ) {
                            my $domain = $1;
                            my $spells = $2;

                            # The DOMAIN
                            if ( $domain =~ /([^=]+)\=(\d+)/ ) {
                                push @xcheck_to_process,
                                    [
                                    'DOMAIN', qq(@@" in "$tag_name$tag_value),
                                    $file_for_error, $line_for_error, $1
                                    ];
                            }
                            else {
                                ewarn( NOTICE,
                                    qq{Invalid syntax for "$domain" in "$tag_name$tag_value"},
                                    $file_for_error,
                                    $line_for_error
                                );
                            }

                            # The SPELL names
                            # To be processed later
                            push @xcheck_to_process,
                                [
                                'SPELL',         qq(@@" in "$tag_name$tag_value),
                                $file_for_error, $line_for_error,
                                split ',',       $spells
                                ];
                        }
                        else {
                            ewarn( NOTICE,
                                qq{Invalid domain/spell list paring in "$tag_name$tag_value"},
                                $file_for_error,
                                $line_for_error
                            );
                            $working_value = "";
                        }
                    }
                }
                else {
                    ewarn( NOTICE,
                        qq{No value found for "$tag_name"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
        }
        elsif ( $tag_name eq 'STAT' ) {
            if ( $linetype eq 'KIT STAT' ) {
                # STAT:STR=17|DEX=10|CON=14|INT=8|WIS=12|CHA=14
                my %stat_count_for = map { $_ => 0 } @valid_system_stats;

                STAT:
                for my $stat_expression (split /[|]/, $tag_value) {
                    my ($stat) = ( $stat_expression =~ / \A ([A-Z]{3}) [=] \d+ \z /xms );
                    if ( !defined $stat ) {
                        # Syntax error
                        ewarn( NOTICE,
                            qq{Invalid syntax for "$stat_expression" in "$tag_name:$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );

                        next STAT;
                    }

                    if ( !exists $stat_count_for{$stat} ) {
                        # The stat is not part of the official list
                        ewarn( NOTICE,
                            qq{Invalid attribute name "$stat" in "$tag_name:$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                    else {
                        $stat_count_for{$stat}++;
                    }
                }

                # We check to see if some stat are repeated
                for my $stat (@valid_system_stats) {
                    if ( $stat_count_for{$stat} > 1 ) {
                        ewarn( NOTICE,
                            qq{Found $stat more then once in "$tag_name:$tag_value"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }
            }
        }
        elsif ( $tag_name eq 'TEMPLATE' && $linetype ne 'PCC' ) {
            # TEMPLATE:<template name>|<template name>|etc.
            push @xcheck_to_process,
                 [  'TEMPLATE',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    (split /[|]/, $tag_value),
                ];
        }
        ######################################################################
        # Here we capture data for later validation
        elsif ( $tag_name eq 'RACESUBTYPE' ) {
            for my $race_subtype (split /[|]/, $tag_value) {
                my $new_race_subtype = $race_subtype;
                if ( $linetype eq 'RACE' ) {
                    # The RACE sub-type are created in the RACE file
                    if ( $race_subtype =~ m{ \A [.] REMOVE [.] }xmsi ) {
                        # The presence of a remove means that we are trying
                        # to modify existing data and not create new one
                        push @xcheck_to_process,
                             [  'RACESUBTYPE',
                                $tag_name,
                                $file_for_error,
                                $line_for_error,
                                $race_subtype,
                            ];
                    }
                    else {
                        $valid_entities{'RACESUBTYPE'}{$race_subtype}++
                    }
                }
                else {
                    # The RACE type found here are not create, we only
                    # get rid of the .REMOVE. part
                    $race_subtype =~ m{ \A [.] REMOVE [.] }xmsi;

                    push @xcheck_to_process,
                         [  'RACESUBTYPE',
                            $tag_name,
                            $file_for_error,
                            $line_for_error,
                            $race_subtype,
                        ];
                }
            }
        }
        elsif ( $tag_name eq 'RACETYPE' ) {
            for my $race_type (split /[|]/, $tag_value) {
                if ( $linetype eq 'RACE' ) {
                    # The RACE type are created in the RACE file
                    if ( $race_type =~ m{ \A [.] REMOVE [.] }xmsi ) {
                        # The presence of a remove means that we are trying
                        # to modify existing data and not create new one
                        push @xcheck_to_process,
                             [  'RACETYPE',
                                $tag_name,
                                $file_for_error,
                                $line_for_error,
                                $race_type,
                            ];
                    }
                    else {
                        $valid_entities{'RACETYPE'}{$race_type}++
                    }
                }
                else {
                    # The RACE type found here are not create, we only
                    # get rid of the .REMOVE. part
                    $race_type =~ m{ \A [.] REMOVE [.] }xmsi;

                    push @xcheck_to_process,
                         [  'RACETYPE',
                            $tag_name,
                            $file_for_error,
                            $line_for_error,
                            $race_type,
                        ];
                }
            }
        }
        elsif ( $tag_name eq 'TYPE' ) {

            # The types go into valid_types
            $valid_types{$linetype}{$_}++ for ( split '\.', $tag_value );
        }
        ######################################################################
        # Tag with numerical values
        elsif (    $tag_name eq 'STARTSKILLPTS'
                || $tag_name eq 'SR'
              ) {

            # These tags should only have a numeribal value
            push @xcheck_to_process,
                 [
                    'DEFINE Variable',
                    qq(@@" in "$tag_name:$tag_value),
                    $file_for_error,
                    $line_for_error,
                    parse_jep(
                        $tag_value,
                        "$tag_name:$tag_value",
                        $file_for_error,
                        $line_for_error
                    ),
                ];
        }
        elsif ( $tag_name eq 'DEFINE' ) {
            my ( $var_name, @formulas ) = split '\|', $tag_value;

            # First we store the DEFINE variable name
            if ($var_name) {
                if ( $var_name =~ /^[a-z][a-z0-9_]*$/i ) {
                    $valid_entities{'DEFINE Variable'}{$var_name}++;

                    #####################################################
                    # Export a list of variable names if requested
                    if ( $conversion_enable{'Export lists'} ) {
                        my $file = $file_for_error;
                        $file =~ tr{/}{\\};
                        print { $filehandle_for{VARIABLE} }
                            qq{"$var_name","$line_for_error","$file"\n};
                    }

                }

                # LOCK.xxx and BASE.xxx are not error (even if they are very ugly)
                elsif ( $var_name !~ /(BASE|LOCK)\.(STR|DEX|CON|INT|WIS|CHA|DVR)/ ) {
                    ewarn( NOTICE,
                        qq{Invalid variable name "$var_name" in "$tag_name:$tag_value"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
            else {
                ewarn( NOTICE,
                    qq{I was not able to find a proper variable name in "$tag_name:$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }

            # Second we deal with the formula
            for my $formula (@formulas) {
                push @xcheck_to_process,
                     [
                        'DEFINE Variable',
                        qq(@@" in "$tag_name:$tag_value),
                        $file_for_error,
                        $line_for_error,
                        parse_jep(
                            $formula,
                            "$tag_name:$tag_value",
                            $file_for_error,
                            $line_for_error
                        )
                     ];
            }
        }
        elsif ( $tag_name eq 'SA' ) {
            my ($var_string) = ( $tag_value =~ /[^|]\|(.*)/ );
            if ($var_string) {
                FORMULA:
                for my $formula ( split '\|', $var_string ) {

                    # Are there any PRE tags in the SA tag.
                    if ( $formula =~ /(^!?PRE[A-Z]*):(.*)/ ) {

                        # A PRExxx tag is present
                        validate_pre_tag($1,
                                         $2,
                                         "$tag_name:$tag_value",
                                         $linetype,
                                         $file_for_error,
                                         $line_for_error
                        );
                        next FORMULA;
                    }

                    push @xcheck_to_process,
                         [
                            'DEFINE Variable',
                            qq(@@" in "$tag_name:$tag_value),
                            $file_for_error,
                            $line_for_error,
                            parse_jep(
                                $formula,
                                "$tag_name:$tag_value",
                                $file_for_error,
                                $line_for_error
                            )
                         ];
                }
            }
        }
        elsif ( $linetype eq 'SPELL'
            && ( $tag_name eq 'TARGETAREA' || $tag_name eq 'DURATION' || $tag_name eq 'DESC' ) )
        {

            # Inline f*#king tags.
            # We need to find CASTERLEVEL between ()
            my $value = $tag_value;
            pos $value = 0;

            FIND_BRACKETS:
            while ( pos $value < length $value ) {
                my $result;
                # Find the first set of ()
                if ( (($result) = Text::Balanced::extract_bracketed( $value, '()' ))
                     && $result
                ) {
                    # Is there a CASTERLEVEL inside?
                    if ( $result =~ / CASTERLEVEL /xmsi ) {
                        push @xcheck_to_process,
                             [
                                'DEFINE Variable',
                                qq(@@" in "$tag_name:$tag_value),
                                $file_for_error,
                                $line_for_error,
                                parse_jep(
                                    $result,
                                    "$tag_name:$tag_value",
                                    $file_for_error,
                                    $line_for_error
                                )
                             ];
                    }
                }
                else {
                    last FIND_BRACKETS;
                }
            }
        }
        elsif ( $tag_name eq 'NATURALATTACKS' ) {

            # NATURALATTACKS:<Natural weapon name>,<List of type>,<attacks>,<damage>|...
            #
            # We must make sure that there are always four , separated parameters
            # between the |.

            for my $entry ( split '\|', $tag_value ) {
                my @parameters = split ',', $entry;

                # Must have 4 parameters
                if ( scalar @parameters == 4 ) {

                    # Parameter 3 is a number
                    ewarn( NOTICE,
                        qq{3rd parameter should be a number in "NATURALATTACKS:$entry"},
                        $file_for_error,
                        $line_for_error
                    ) unless $parameters[2] =~ /^\*?\d+$/;

                    # Are the types valid EQUIPMENT types?
                    push @xcheck_to_process,
                        [
                        'EQUIPMENT TYPE', qq(@@" in "$tag_name:$entry),
                        $file_for_error,  $line_for_error,
                        grep { !$valid_NATURALATTACKS_type{$_} } split '\.', $parameters[1]
                        ];
                }
                else {
                    ewarn( NOTICE,
                        qq{Wrong number of parameter for "NATURALATTACKS:$entry"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
        }
        elsif ( $tag_name eq 'CHANGEPROF' ) {

# "CHANGEPROF:" <list of weapons> "=" <new prof> { "|"  <list of weapons> "=" <new prof> }*
# <list of weapons> := ( <weapon> | "TYPE=" <weapon type> ) { "," ( <weapon> | "TYPE=" <weapon type> ) }*

            for my $entry ( split '\|', $tag_value ) {
                if ( $entry =~ /^([^=]+)=([^=]+)$/ ) {
                    my ( $list_of_weapons, $new_prof ) = ( $1, $2 );

                    # First, the weapons (equipment)
                    push @xcheck_to_process,
                        [
                        'EQUIPMENT', $tag_name, $file_for_error, $line_for_error,
                        split ',',   $list_of_weapons
                        ];

                    # Second, the weapon prof.
                    push @xcheck_to_process,
                        [
                        'WEAPONPROF', $tag_name, $file_for_error, $line_for_error,
                        $new_prof
                        ];

                }
                else {
                }
            }
        }

##  elsif($tag_name eq 'CHOOSE')
##  {
##    # Is the CHOOSE type valid?
##    my ($choose_type) = ($tag_value =~ /^([^=|]+)/);
##
##    if($choose_type && !exists $token_CHOOSE_tag{$choose_type})
##    {
##      if(index($choose_type,' ') != -1)
##      {
##        # There is a space in the choose type, it must be a
##        # typeless CHOOSE (darn).
##        ewarn( NOTICE,  "** Typeless CHOOSE found: \"$tag_name:$tag_value\" in $linetype.",
##               $file_for_error, $line_for_error );
##      }
##      else
##      {
##        $count_tags{"Invalid"}{"Total"}{"$tag_name:$choose_type"}++;
##        $count_tags{"Invalid"}{$linetype}{"$tag_name:$choose_type"}++;
##        ewarn( NOTICE,  "Invalid CHOOSE:$choose_type tag \"$tag_name:$tag_value\" found in $linetype.",
##               $file_for_error, $line_for_error );
##      }
##    }
##    elsif(!$choose_type)
##    {
##      $count_tags{"Invalid"}{"Total"}{"CHOOSE"}++;
##      $count_tags{"Invalid"}{$linetype}{"CHOOSE"}++;
##      ewarn( NOTICE,  "Invalid CHOOSE tag \"$tag_name:$tag_value\" found in $linetype",
##        $file_for_error, $line_for_error );
##    }
##  }

    }

}    # BEGIN End

###############################################################
# validate_pre_tag
# ----------------
#
# Validate the PRExxx tags. This function is reentrant and call
# be called recursivly.

sub validate_pre_tag {
    my ($tag_name,              # Name of the tag (before the :)
        $tag_value,             # Value of the tag (after the :)
        $enclosing_tag,         # When the PRExxx tag is used in another tag
        $linetype,              # Type for the courent file
        $file_for_error,        # Name of the courent file
        $line_for_error         # Number of the courent line
    ) = @_;

    if ( !length($tag_value) && $tag_name ne "PRE:.CLEAR" ) {

        # No value found
        my $message = qq{Check for missing ":", no value for "$tag_name"};
        $message .= qq{ found in "$enclosing_tag"} if $enclosing_tag;

        ewarn( WARNING, $message, $file_for_error, $line_for_error );

        return;
    }

    ewarn( DEBUG,
        qq{validate_pre_tag: $tag_name; $tag_value; $enclosing_tag; $linetype;},
        $file_for_error,
        $line_for_error
    ) if ( $cl_options{warning_level} == DEBUG );

    my $pretag = $tag_name;
    my $is_neg = 1 if $pretag =~ s/^!(.*)/$1/;
    my $comp_op;

    # Special treatment for tags ending in MULT because of PREMULT and
    # PRESKILLMULT
    ($comp_op) = ( $pretag =~ s/(.*)(EQ|GT|GTEQ|LT|LTEQ|NEQ)$/$1/ )[1]
        unless $pretag =~ /MULT$/;

    if ( $pretag eq 'PRECLASS' || $pretag eq 'PRECLASSLEVELMAX' ) {

        #PRECLASS:number,Class,Class=ClassLevel
        my @classes = split ',', $tag_value;

        if ( $classes[0] =~ /^\d+$/ ) {
            shift @classes;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        push @xcheck_to_process, [ 'CLASS', $tag_name, $file_for_error, $line_for_error, @classes ];
    }
    elsif ( $pretag eq 'PRECHECK' || $pretag eq 'PRECHECKBASE') {
        # PRECHECK:<number>,<check equal value list>
        # PRECHECKBASE:<number>,<check equal value list>
        # <check equal value list> := <check name> "=" <number>
        my @items = split q{,}, $tag_value;

        if ( $items[0] =~ / \A \d+ \z /xms ) {
            shift @items;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        for my $item ( @items ) {
            if ( my ($check_name,$value) = ( $item =~ / \A ( \w+ ) = ( \d+ ) \z /xms ) ) {
                if ( !exists $valid_check_name{$check_name} ) {
                    ewarn( NOTICE,
                        qq{Invalid save check name "$check_name" found in "$tag_name:$tag_value"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
            else {
                ewarn( NOTICE,
                    qq{$pretag syntax error in "$item" found in "$tag_name:$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }

    }
    elsif ( $pretag eq 'PRECSKILL' ) {

        # We get the list of skills and skill types
        my @skills = split ',', $tag_value;

        if ( $skills[0] =~ / \A \d+ \z /xms ) {
            shift @skills;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        push @xcheck_to_process, [ 'SKILL', $tag_name, $file_for_error, $line_for_error, @skills ];
    }
    elsif ( $pretag eq 'PREDEITY' ) {
        #PREDEITY:Y
        #PREDEITY:YES
        #PREDEITY:N
        #PREDEITY:NO
        #PREDEITY:<deity name>,<deity name>,etc.
        if ( $tag_value !~ / \A (?: Y(?:ES)? | N[O]? ) \z /xms ) {
            #We ignore the single yes or no
            push @xcheck_to_process,
                 [
                    'DEITY',
                    $tag_name,
                    $file_for_error,
                    $line_for_error,
                    (split /[,]/, $tag_value),
                 ];
        }
    }
    elsif ( $pretag eq 'PREDEITYDOMAIN' || $pretag eq 'PREDOMAIN' ) {

        #PREDOMAIN:number,Domain,Domain
        my @domains = split ',', $tag_value;

        if ( $domains[0] =~ /^\d+$/ ) {
            shift @domains;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        push @xcheck_to_process,
            [ 'DOMAIN', $tag_name, $file_for_error, $line_for_error, @domains ];
    }
    elsif ( $pretag eq 'PREFEAT' ) {

        # PREFEAT:number,feat,feat,TYPE=type

        # We get the list of feats and feat types
        my @feats = embedded_coma_split($tag_value);

        if ( $feats[0] =~ / \A \d+ \z /xms ) {
            shift @feats;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        push @xcheck_to_process, [ 'FEAT', $tag_name, $file_for_error, $line_for_error, @feats ];
    }
    elsif ( $pretag eq 'PREITEM' ) {

        # PRETIEM:number,item,TYPE=itemtype
        # The list of items may include () with embeded coma
        my @items = embedded_coma_split($tag_value);

        if ( $items[0] =~ / \A \d+ \z /xms ) {
            shift @items;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        push @xcheck_to_process,
            [ 'EQUIPMENT', $tag_name, $file_for_error, $line_for_error, @items ];
    }
    elsif ( $pretag eq 'PRELANG' ) {

        # PRELANG:number,language,language,TYPE=type

        # We get the list of feats and feat types
        my @languages = split ',', $tag_value;

        if ( $languages[0] =~ / \A \d+ \z /xms ) {
            shift @languages;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        push @xcheck_to_process,
            [
            'LANGUAGE', $tag_name, $file_for_error, $line_for_error,
            grep { $_ ne 'ANY' } @languages
            ];
    }
    elsif ( $pretag eq 'PREMOVE' ) {

        # PREMOVE:[<number>,]<move>=<number>,<move>=<number>,...

        my @moves = split ',', $tag_value;

        if ( $moves[0] =~ / \A \d+ \z /xms ) {
            shift @moves;    # We drop the number at the beginning
        }
        else {

            # We don't print the warning because the tag has not been converted yet
            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        for my $move (@moves) {

            # Verify that the =<number> is there
            if ( $move =~ /^([^=]*)=([^=]*)$/ ) {
                push @xcheck_to_process,
                    [
                    'MOVE Type', $tag_name, $file_for_error, $line_for_error,
                    $1
                    ];

                # The value should be a number
                my $value = $2;
                unless ( $value =~ /^\d+$/ ) {
                    my $message
                        = qq{Not a number after the = for "$move" in "$tag_name:$tag_value"};
                    $message .= qq{ found in "$enclosing_tag"} if $enclosing_tag;
                    ewarn( NOTICE, $message, $file_for_error, $line_for_error );
                }
            }
            else {
                my $message = qq{Invalid "$move" in "$tag_name:$tag_value"};
                $message .= qq{ found in "$enclosing_tag"} if $enclosing_tag;
                ewarn( NOTICE, $message, $file_for_error, $line_for_error );
            }
        }
    }
    elsif ( $pretag eq 'PREMULT' ) {

        # This tag is the reason why validate_pre_tag exists
        # PREMULT:x,[PRExxx 1],[PRExxx 2]
        # We need for find all the [] and call validate_pre_tag with the content

        my $working_value = $tag_value;
        my $inside;

        # We add only one level of PREMULT to the error message.
        my $emb_tag_name;
        if ($enclosing_tag) {
            $emb_tag_name = $enclosing_tag;
            $emb_tag_name .= ':PREMULT' unless $emb_tag_name =~ /PREMULT$/;
        }
        else {
            $emb_tag_name .= 'PREMULT';
        }

        #    while($inside = Text::Balanced::extract_bracketed($working_value, '[]', qr([^[]*)))
        FIND_BRACE:
        while ($working_value) {
            ( $inside, $working_value )
                = Text::Balanced::extract_bracketed( $working_value, '[]', qr{[^[]*} );

            last FIND_BRACE if !$inside;

            # We extract what we need
            my ( $tag, $value ) = ( $inside =~ /^\[(!?PRE[A-Z]+):(.*)\]$/ );
            if ($tag) {
                validate_pre_tag($tag,
                                 $value,
                                 $emb_tag_name,
                                 $linetype,
                                 $file_for_error,
                                 $line_for_error
                );
            }
            else {

                # No PRExxx tag found inside the PREMULT
                ewarn( WARNING,
                    qq{No valid PRExxx tag found in "$inside" inside "PREMULT:$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
    }
    elsif ( $pretag eq 'PRERACE' ) {
        # We get the list of races
        my @races_tmp = split ',', $tag_value;

        # Validate that the first entry is a number
        if ( $races_tmp[0] =~ / \A \d+ \z /xms ) {
            shift @races_tmp;    # We drop the number at the beginning
        }
        else {
            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        my ( @races, @races_wild );

        for my $race (@races_tmp)
        {
            if ( $race =~ / (.*?) [%] (.*?) /xms ) {
                # Special case for PRERACE:xxx%
                my $race_wild  = $1;
                my $after_wild = $2;

                push @races_wild, $race_wild;

                if ( $after_wild ne q{} ) {
                    ewarn( NOTICE,
                           qq{% used in wild card context should end the race name in "$race"},
                           $file_for_error,
                           $line_for_error
                    );
                }

                # For now, we warn and do nothing else.
                ewarn( INFO,
                       qq{Not able to validate "$race" in "PRERACE:$tag_value"},
                       $file_for_error,
                       $line_for_error
                );
            }
            else {
                push @races, $race;
            }
        }

        push @xcheck_to_process, [ 'RACE', $tag_name, $file_for_error, $line_for_error, @races ];
#        push @xcheck_to_process, [ 'RACE%;PRERACE', $tag_name, $file_for_error, $line_for_error, @races_wild ];
    }
    elsif ( $pretag eq 'PRESKILL' ) {

        # We get the list of skills and skill types
        my @skills = split ',', $tag_value;

        if ( $skills[0] =~ / \A \d+ \z /xms ) {
            shift @skills;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        push @xcheck_to_process, [ 'SKILL', $tag_name, $file_for_error, $line_for_error, @skills ];
    }
    elsif ( $pretag eq 'PRESPELL' ) {

        # We get the list of skills and skill types
        my @spells = split ',', $tag_value;

        if ( $spells[0] =~ / \A \d+ \z /xms ) {
            shift @spells;    # We drop the number at the beginning
        }
        else {

            # The PREtag doesn't begin by a number
            warn_deprecate( "$tag_name:$tag_value",
                $file_for_error,
                $line_for_error,
                $enclosing_tag
            );
        }

        push @xcheck_to_process,
            [ 'SPELL', "$tag_name:@@", $file_for_error, $line_for_error, @spells ];
    }
    elsif ( $pretag eq 'PREVAR' ) {
        my ( $var_name, @formulas ) = split ',', $tag_value;

        push @xcheck_to_process,
             [
                'DEFINE Variable',
                qq(@@" in "$tag_name:$tag_value),
                $file_for_error,
                $line_for_error,
                $var_name,
             ];

        for my $formula (@formulas) {
            push @xcheck_to_process,
                 [
                    'DEFINE Variable',
                    qq(@@" in "$tag_name:$tag_value),
                    $file_for_error,
                    $line_for_error,
                    parse_jep(
                        $formula,
                        "$tag_name:$tag_value",
                        $file_for_error,
                        $line_for_error
                    ),
                 ];
        }
    }

    # Check for PRExxx that do not exists. We only check the
    # tags that are embeded since parse_tag already took care
    # of the PRExxx tags on the entry lines.
    elsif ( $enclosing_tag && !exists $PRE_Tags{$tag_name} ) {
        ewarn( NOTICE,
            qq{Unknown PRExxx tag "$tag_name" found in "$enclosing_tag"},
            $file_for_error,
            $line_for_error
        );
    }
}

###############################################################
# add_to_xcheck_tables
# --------------------
#
# This function adds entries that will need to cross-checked
# against existing entities.
#
# It also filter the global entries and other weirdness.
#
# Pamameter:  $entry_type       Type of the entry that must be cheacked
#             $tag_name         Name of the tag for message display
#                               If tag name contains @@, it will be replaced
#                               by the entry text from the list for the message.
#                               Otherwise, the format $tag_name:$list_entry will
#                               be used.
#             $file_for_error   Name of the courent file
#             $line_for_error   Number of the courent line
#             @list             List of entries to be added

BEGIN {

    # Variables names that must be skiped for the DEFINE variable section
    # entry type.

    my %Hardcoded_Variables = map { $_ => 1 } (
        # Real hardcoded variables
        'ACCHECK',
#        'ARMORACCHECK',
#        'BAB',
        'BASESPELLSTAT',
        '%CHOICE',
        'CASTERLEVEL',
        'CL',
#        'CLASSLEVEL',
        'ENCUMBERANCE',
#        'GRAPPLESIZEMOD',
        'HD',
        'MOVEBASE',
        'SIZE',
#        'SPELLSTAT',
        'TL',

        # Functions for the JEP parser
        'ceil',
        'floor',
        'if',
        'min',
        'max',
        'roll',
        'var',
    );

    sub add_to_xcheck_tables {
        my (
            $entry_type,           # Type of the entry that must be cheacked
            $tag_name,             # Name of the tag for message display
                                   # If tag name contains @@, it will be replaced
                                   # by the entry text from the list for the message.
                                   # Otherwise, the format $tag_name:$list_entry will
                                   # be used.
            $file_for_error,       # Name of the courent file
            $line_for_error,       # Number of the courent line
            @list                  # List of entries to be added
        ) = ( @_, "" );

        # If $file_for_error is not under $cl_options{input_path}, we do not add
        # it to be validated. This happens when a -basepath parameter is used
        # with the script.
        return if $file_for_error !~ / \A $cl_options{input_path} /xmsi;

        # We removed the empty elements in the list
        @list = grep { $_ ne "" } @list;

        # If the list of entry is empty, we retrun immediately
        return if scalar @list == 0;

        # We set $tag_name properly for the substitution
        $tag_name .= ":@@" unless $tag_name =~ /@@/;

        if ( $entry_type eq 'CLASS' ) {
            for my $class (@list) {

                # Remove the =level if there is one
                $class =~ s/(.*)=\d+$/$1/;

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$class/;

                # Spellcaster is a special PCGEN keyword, not a real class
                push @{ $referer{'CLASS'}{$class} },
                    [ $message_name, $file_for_error, $line_for_error ]
                    if ( uc($class) ne "SPELLCASTER"
                    && uc($class) ne "SPELLCASTER.ARCANE"
                    && uc($class) ne "SPELLCASTER.DIVINE"
                    && uc($class) ne "SPELLCASTER.PSIONIC" );
            }
        }
        elsif ( $entry_type eq 'DEFINE Variable' ) {
            VARIABLE:
            for my $var (@list) {

                # We skip, the COUNT[] thingy must not be validated
                next VARIABLE if $var =~ /^COUNT\[/;

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$var/;

                push @{ $referer{'DEFINE Variable'}{$var} },
                    [ $message_name, $file_for_error, $line_for_error ]
                    unless $Hardcoded_Variables{$var};
            }
        }
        elsif ( $entry_type eq 'DEITY' ) {
            for my $deity (@list) {
                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$deity/;

                push @{ $referer{'DEITY'}{$deity} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'DOMAIN' ) {
            for my $domain (@list) {

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$domain/;

                push @{ $referer{'DOMAIN'}{$domain} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'EQUIPMENT' ) {
            for my $equipment (@list) {

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$equipment/;

                if ( $equipment =~ /^TYPE=(.*)/ ) {
                    push @{ $referer_types{'EQUIPMENT'}{$1} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
                else {
                    push @{ $referer{'EQUIPMENT'}{$equipment} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
            }
        }
        elsif ( $entry_type eq 'EQUIPMENT TYPE' ) {
            for my $type (@list) {

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$type/;

                push @{ $referer_types{'EQUIPMENT'}{$type} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'EQUIPMOD Key' ) {
            for my $key (@list) {

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$key/;

                push @{ $referer{'EQUIPMOD Key'}{$key} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'FEAT' ) {
            FEAT:
            for my $feat (@list) {

                # We ignore CHECKMULT if used within a PREFEAT tag
                next FEAT if $feat eq 'CHECKMULT' && $tag_name =~ /PREFEAT/;

                # We ignore LIST if used within an ADD:FEAT tag
                next FEAT if $feat eq 'LIST' && $tag_name eq 'ADD:FEAT';

                # We stript the () if any
                if ( $feat =~ /(.*?[^ ]) ?\((.*)\)/ ) {

                    # We check to see if the FEAT is a compond tag
                    if ( $valid_sub_entities{'FEAT'}{$1} ) {
                        my $original_feat = $feat;
                        my $feat_to_check = $feat = $1;
                        my $entity        = $2;
                        my $sub_tag_name  = $tag_name;
                        $sub_tag_name =~ s/@@/$feat (@@)/;

                        # Find the real entity type in case of FEAT=
                        FEAT_ENTITY:
                        while ( $valid_sub_entities{'FEAT'}{$feat_to_check} =~ /^FEAT=(.*)/ ) {
                            $feat_to_check = $1;
                            if ( !exists $valid_sub_entities{'FEAT'}{$feat_to_check} ) {
                                ewarn( NOTICE,
                                    qq{Cannot find the sub-entity for "$original_feat"},
                                    $file_for_error,
                                    $line_for_error
                                );
                                $feat_to_check = "";
                                last FEAT_ENTITY;
                            }
                        }

                        add_to_xcheck_tables(
                            $valid_sub_entities{'FEAT'}{$feat_to_check},
                            $sub_tag_name,
                            $file_for_error,
                            $line_for_error,
                            $entity
                        ) if $feat_to_check && $entity ne 'Ad-Lib';
                    }
                }

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$feat/;

                if ( $feat =~ /^TYPE[=.](.*)/ ) {
                    push @{ $referer_types{'FEAT'}{$1} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
                else {
                    push @{ $referer{'FEAT'}{$feat} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
            }
        }
        elsif ( $entry_type eq 'KIT STARTPACK' ) {
            for my $kit (@list) {

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$kit/;

                push @{ $referer{'KIT STARTPACK'}{$kit} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'LANGUAGE' ) {
            for my $language (@list) {

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$language/;

                if ( $language =~ /^TYPE=(.*)/ ) {
                    push @{ $referer_types{'LANGUAGE'}{$1} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
                else {
                    push @{ $referer{'LANGUAGE'}{$language} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
            }
        }
        elsif ( $entry_type eq 'MOVE Type' ) {
            MOVE_TYPE:
            for my $move (@list) {

                # The ALL move type is always valid
                next MOVE_TYPE if $move eq 'ALL';

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$move/;

                push @{ $referer{'MOVE Type'}{$move} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'RACE' ) {
            for my $race (@list) {
                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$race/;

                if ( $race =~ / \A TYPE= (.*) /xms ) {
                    push @{ $referer_types{'RACE'}{$1} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
                elsif ( $race =~ / \A RACETYPE= (.*) /xms ) {
                    push @{ $referer{'RACETYPE'}{$1} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
                elsif ( $race =~ / \A RACESUBTYPE= (.*) /xms ) {
                    push @{ $referer{'RACESUBTYPE'}{$1} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
                else {
                    push @{ $referer{'RACE'}{$race} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
            }
        }
        elsif ( $entry_type eq 'RACE TYPE' ) {
            for my $race_type (@list) {
                # RACE TYPE is use for TYPE tags in RACE object
                my $message_name = $tag_name;
                $message_name =~ s/@@/$race_type/;

                push @{ $referer_types{'RACE'}{$race_type} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'RACESUBTYPE' ) {
            for my $race_subtype (@list) {
                my $message_name = $tag_name;
                $message_name =~ s/@@/$race_subtype/;

                # The RACESUBTYPE can be .REMOVE.<race subtype name>
                $race_subtype =~ s{ \A [.] REMOVE [.] }{}xms;

                push @{ $referer{'RACESUBTYPE'}{$race_subtype} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'RACETYPE' ) {
            for my $race_type (@list) {
                my $message_name = $tag_name;
                $message_name =~ s/@@/$race_type/;

                # The RACETYPE can be .REMOVE.<race type name>
                $race_type =~ s{ \A [.] REMOVE [.] }{}xms;

                push @{ $referer{'RACETYPE'}{$race_type} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'SKILL' ) {
            SKILL:
            for my $skill (@list) {

                # LIST alone is OK, it is a special variable
                # used to tie in the CHOOSE result
                next SKILL if $skill eq 'LIST';

                # Remove the =level if there is one
                $skill =~ s/(.*)=\d+$/$1/;

                # If there are (), we must verify if it is
                # a compond skill
                if ( $skill =~ /(.*?[^ ]) ?\((.*)\)/ ) {

                    # We check to see if the FEAT is a compond tag
                    if ( $valid_sub_entities{'SKILL'}{$1} ) {
                        $skill = $1;
                        my $entity = $2;

                        my $sub_tag_name = $tag_name;
                        $sub_tag_name =~ s/@@/$skill (@@)/;

                        add_to_xcheck_tables(
                            $valid_sub_entities{'SKILL'}{$skill},
                            $sub_tag_name,
                            $file_for_error,
                            $line_for_error,
                            $entity
                        ) if $entity ne 'Ad-Lib';
                    }
                }

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$skill/;

                if ( $skill =~ / \A TYPE [.=] (.*) /xms ) {
                    push @{ $referer_types{'SKILL'}{$1} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
                else {
                    push @{ $referer{'SKILL'}{$skill} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
            }
        }
        elsif ( $entry_type eq 'SPELL' ) {
            for my $spell (@list) {

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$spell/;

                if ( $spell =~ /^TYPE=(.*)/ ) {
                    push @{ $referer_types{'SPELL'}{$1} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
                else {
                    push @{ $referer{'SPELL'}{$spell} },
                        [ $message_name, $file_for_error, $line_for_error ];
                }
            }
        }
        elsif ( $entry_type eq 'TEMPLATE' ) {
            for my $template (@list) {
                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$template/;

                # We clean up the unwanted stuff
                my $template_copy = $template;
                $template_copy =~ s/ CHOOSE: //xms;
                $message_name =~ s/ CHOOSE: //xms;

                push @{ $referer{'TEMPLATE'}{$template_copy} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        elsif ( $entry_type eq 'WEAPONPROF' ) {
#            for my $weaponprof (@list) {
#
#                # Put the entry name in place
#                my $message_name = $tag_name;
#                $message_name =~ s/@@/$weaponprof/;
#
#                if ( $spell =~ /^TYPE=(.*)/ ) {
#                    push @{ $referer_types{'WEAPONPROF'}{$1} },
#                        [ $message_name, $file_for_error, $line_for_error ];
#                }
#                else {
#                    push @{ $referer{'WEAPONPROF'}{$weaponprof} },
#                        [ $message_name, $file_for_error, $line_for_error ];
#                }
#            }
        }
        elsif ( $entry_type eq 'SPELL_SCHOOL' || $entry_type eq 'Ad-Lib' ) {
            # Nothing is done yet.
        }
        elsif ( $entry_type =~ /,/ ) {

            # There is a , in the name so it is a special
            # validation case that is defered until the validation time.
            # In short, the entry must exists in one of the type list.
            for my $entry (@list) {

                # Put the entry name in place
                my $message_name = $tag_name;
                $message_name =~ s/@@/$entry/;

                push @{ $referer{$entry_type}{$entry} },
                    [ $message_name, $file_for_error, $line_for_error ];
            }
        }
        else {
            ewarn( ERROR,
                "Invalid Entry type for $tag_name (add_to_xcheck_tables): $entry_type",
                $file_for_error,
                $line_for_error
            );
        }
    }

}    # BEGIN end

###############################################################
# parse_jep
# ----------------
#
# Extract the variable names from a PCGEN formula
#
# Parameter:  $formula        : String containing the formula
#             $tag            : Tag containing the formula
#             $file_for_error : Filename to use with ewarn
#             $line_for_error : Line number to use with ewarn

#open FORMULA, ">formula.txt" or die "Can't open formula: $OS_ERROR";

sub extract_var_name {
    my ( $formula, $tag, $file_for_error, $line_for_error ) = @_;

    return () unless $formula;

#    my @variables = parse_jep(@_);

    #  print FORMULA "$formula\n" unless $formula =~ /^[0-9]+$/;

    # Will hold the result values
    my @variable_names = ();

    # We remove the COUNT[xxx] from the formulas
    while ( $formula =~ s/(COUNT\[[^]]*\])//g ) {
        push @variable_names, $1;
    }

    # We have to catch all the VAR=Funky Text before anything else
    while ( $formula =~ s/([a-z][a-z0-9_]*=[a-z0-9_ ='{}]*)//i ) {
        my @values = split '=', $1;
        if ( @values > 2 ) {

            # There should only be one = per variable
            ewarn( WARNING,
                qq{Too many = in "$1" found in "$tag"},
                $file_for_error,
                $line_for_error
            );
        }
        elsif (
            $values[0] eq 'BL' ||    # [ 1104117 ] BL is a valid variable, like CL
            $values[0] eq 'CL' || $values[0] eq 'CLASS' || $values[0] eq 'CLASSLEVEL'
            )
        {

            # Convert {} to () for proper validation
            $values[1] =~ tr/{}/()/;
            push @xcheck_to_process,
                 [
                    'CLASS',         qq(@@" in "$tag),
                    $file_for_error, $line_for_error,
                    $values[1]
                 ];
        }
        elsif ($values[0] eq 'SKILLRANK'
            || $values[0] eq 'SKILLTOTAL' )
        {

            # Convert {} to () for proper validation
            $values[1] =~ tr/{}/()/;
            push @xcheck_to_process,
                 [
                    'SKILL',         qq(@@" in "$tag),
                    $file_for_error, $line_for_error,
                    $values[1]
                 ];
        }
        else {
            ewarn( NOTICE,
                qq{Invalid variable "$values[0]" before the = in "$1" found in "$tag"},
                $file_for_error,
                $line_for_error
            );
        }
    }

    # Variables begin with a letter or the % and are followed
    # by letters, numbers, or the _
    VAR_NAME:
    for my $var_name ( $formula =~ /([a-z%][a-z0-9_]*)/gi ) {
        # If it's an operator, we skip it.
        next VAR_NAME
            if ( index( $var_name, 'MAX'   ) != -1
              || index( $var_name, 'MIN'   ) != -1
              || index( $var_name, 'TRUNC' ) != -1
        );

        push @variable_names, $var_name;
    }

    return @variable_names;
}

###############################################################
###############################################################
####
#### Start of parse_jep and related function closure
####

BEGIN {
    # List of keywords Jep functions names. The third row is for
    # functions defined by the PCGen that does not exists in
    # the standard Jep library.
    my %is_jep_function = map { $_ => 1 } qw(
        sin     cos     tan     asin    acos    atan    atan2       sinh
        cosh    tanh    asinh   acosh   atanh   ln      log         exp
        abs     rand    mod     sqrt    sum     if      str

        ceil    cl      floor   min     max     roll    skillinfo   var
    );

    # Definition of a valid Jep identifiers. Note that all functions are
    # identifiers followed by a parentesis.
    my $is_ident = qr{ [a-z_][a-z_0-9]* }xmsi;

    # Valid Jep operators
    my $is_operators_text = join( '|', map { quotemeta } (
                    '^', '%',  '/',  '*',  '+',  '-', '<=', '>=',
                    '<', '>', '!=', '==', '&&', '||', '=',  '!',
                                       )
                            );

    my $is_operator = qr{ $is_operators_text }xms;

    my $is_number = qr{ (?: \d+ (?: [.] \d* )? ) | (?: [.] \d+ ) }xms;

###############################################################
# parse_jep
# ---------
#
# Parse a Jep formula expression and return a list of variables
# found.
#
# parse_jep is just a stub to call parse_jep_rec the first time
#
# Parameter:  $formula        : String containing the formula
#             $tag            : Tag containing the formula
#             $file_for_error : Filename to use with ewarn
#             $line_for_error : Line number to use with ewarn
#
# Return a list of variables names found in the formula

    sub parse_jep {
        # We abosulutely need to be called in array context.
        croak q{parse_jep must be called in list context}
            if !wantarray;

        # Sanity check on the number of parameters
        croak q{Wrong number of parameters for parse_jep}
            if scalar @_ != 4;
        # If the -nojep command line option was used, we
        # call the old parser
        if ( $cl_options{nojep} ) {
            return extract_var_name(@_);
        }
        elso {
            return parse_jep_rec( @_, NO );
        }
    }

###############################################################
# parse_jep_rec
# -------------
#
# Parse a Jep formula expression and return a list of variables
# found.
#
# Parameter:  $formula        : String containing the formula
#             $tag            : Tag containing the formula
#             $file_for_error : Filename to use with ewarn
#             $line_for_error : Line number to use with ewarn
#             $is_param       : Indicate if the Jep expression
#                               is a function parameter
#
# Return a list of variables names found in the formula

    sub parse_jep_rec {
        my ($formula, $tag, $file_for_error, $line_for_error, $is_param) = @_;

        return () if !defined $formula;

        my @variables_found = ();       # Will contain the return values
        my $last_token      = q{};      # Only use for error messages
        my $last_token_type = q{};

        pos $formula = 0;

        while ( pos $formula < length $formula ) {
            # Identifiers are only valid after an operator or a separator
            if ( my ($ident) = ( $formula =~ / \G ( $is_ident ) /xmsgc ) ) {
                # It's an identifier or a function
                if (   $last_token_type
                     && $last_token_type ne 'operator'
                     && $last_token_type ne 'separator'
                ) {
                        # We "eat" the rest of the string and report an error
                        my ($bogus_text) = ( $formula =~ / \G (.*) /xmsgc );
                        ewarn( NOTICE,
                            qq{Jep syntax error near "$ident$bogus_text" found in "$tag"},
                            $file_for_error,
                            $line_for_error
                        );
                }
                # Indentificator followed by bracket = function
                elsif ( $formula =~ / \G [(] /xmsgc ) {
                    # It's a function, is it valid?
                    if ( !$is_jep_function{$ident} ) {
                        ewarn ( NOTICE,
                            qq{Not a valid Jep function: $ident() found in $tag},
                            $file_for_error,
                            $line_for_error
                        );
                    }

                    # Reset the regex position just before the parantesis
                    pos $formula = pos($formula) - 1;

                    # We extract the function parameters
                    my ($extracted_text)
                        = Text::Balanced::extract_bracketed( $formula, '(")' );

                    carp $formula if !$extracted_text;

                    $last_token = "$ident$extracted_text";
                    $last_token_type = 'function';

                    # We remove the enclosing brackets
                    ($extracted_text) = ( $extracted_text =~ / \A [(] ( .* ) [)] \z /xms );

                    # For the var() function, we call the old parser
                    if ( $ident eq 'var' ) {
                        my ($var_text,$reminder) = Text::Balanced::extract_delimited( $extracted_text );

                        # Verify that the values are between ""
                        if ( $var_text ne q{} && $reminder eq q{} ) {
                            # Revove the ""
                            ($var_text) = ( $var_text =~ / \A [\"] ( .* ) [\"] \z /xms );

                            push @variables_found,
                                 extract_var_name(
                                    $var_text,
                                    $tag,
                                    $file_for_error,
                                    $line_for_error
                                 );
                        }
                        else {
                            ewarn( NOTICE,
                                qq{Quote missing for the var() parameter in "$tag"},
                                $file_for_error,
                                $line_for_error
                            );

                            # We use the original extracted text with the old var parser
                            push @variables_found,
                                 extract_var_name(
                                    $extracted_text,
                                    $tag,
                                    $file_for_error,
                                    $line_for_error
                                 );
                        }
                    }
                    else {
                        # Otherwise, each of the function parameters should be a valid Jep expression
                        push @variables_found,
                             parse_jep_rec( $extracted_text, $tag, $file_for_error, $line_for_error, YES );
                    }
                }
                else {
                    # It's an identifier
                    push @variables_found, $ident;
                    $last_token = $ident;
                    $last_token_type = 'ident';
                }
            }
            elsif ( my ($operator) = ( $formula =~ / \G ( $is_operator ) /xmsgc ) ) {
                # It's an operator

                if ( $operator eq '=' ) {
                    if ( $last_token_type eq 'ident' ) {
                        ewarn( NOTICE,
                            qq{Forgot to use var()? Dubious use of Jep variable assignation near }
                                . qq{"$last_token$operator" in "$tag"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                    else {
                        ewarn( NOTICE,
                            qq{Did you want the logical "=="? Dubious use of Jep variable assignation near }
                                . qq{"$last_token$operator" in "$tag"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }

                $last_token = $operator;
                $last_token_type = 'operator';
            }
            elsif ( $formula =~ / \G [(] /xmsgc ) {
                # Reset the regex position just before the bracket
                pos $formula = pos($formula) - 1;

                # Extract what is between the () and call recursivly
                my ($extracted_text)
                    = Text::Balanced::extract_bracketed( $formula, '(")' );

                if ($extracted_text) {
                    $last_token = $extracted_text;
                    $last_token_type = 'expression';

                    # Remove the outside brackets
                    ($extracted_text) = ( $extracted_text =~ / \A [(] ( .* ) [)] \z /xms );

                    # Recursive call
                    push @variables_found,
                         parse_jep_rec( $extracted_text, $tag, $file_for_error, $line_for_error, NO );
                }
                else {
                    # We "eat" the rest of the string and report an error
                    my ($bogus_text) = ( $formula =~ / \G (.*) /xmsgc );
                    ewarn( NOTICE,
                        qq{Unbalance () in "$bogus_text" found in "$tag"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
            elsif ( my ($number) = ( $formula =~ / \G ( $is_number ) /xmsgc ) ) {
                # It's a number
                $last_token = $number;
                $last_token_type = 'number';
            }
            elsif ( $formula =~ / \G [\"'] /xmsgc ) {
                # It's a string
                # Reset the regex position just before the quote
                pos $formula = pos($formula) - 1;

                # Extract what is between the () and call recursivly
                my ($extracted_text)
                    = Text::Balanced::extract_delimited( $formula );

                if ($extracted_text) {
                    $last_token = $extracted_text;
                    $last_token_type = 'string';
                }
                else {
                    # We "eat" the rest of the string and report an error
                    my ($bogus_text) = ( $formula =~ / \G (.*) /xmsgc );
                    ewarn( NOTICE,
                        qq{Unbalance quote in "$bogus_text" found in "$tag"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
            elsif ( my ($separator) = ( $formula =~ / \G ( [,] ) /xmsgc ) ) {
                # It's a coma
                if ( $is_param == NO ) {
                    # Coma are allowed only as parameter separator
                    my ($bogus_text) = ( $formula =~ / \G (.*) /xmsgc );
                    ewarn ( NOTICE,
                        qq{Jep syntax error found near "$separator$bogus_text" in "$tag"},
                        $file_for_error,
                        $line_for_error
                    );
                }

                $last_token = $separator;
                $last_token_type = 'separator';
            }
            elsif ( $formula =~ / \G \s+ /xmsgc ) {
                # Spaces are allowed in Jep expressions, we simply ignore them
            }
            else {
                # If we are here, all is not well
                my ($bogus_text) = ( $formula =~ / \G (.*) /xmsgc );
                ewarn ( NOTICE,
                    qq{Jep syntax error found near "$bogus_text" in "$tag"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }

        return @variables_found;
    }

}

####
#### End of parse_jep and related function closure
####
###############################################################
###############################################################


###############################################################
# additionnal_tag_parsing
# -----------------------
#
# This function does additional parsing on each line once
# they have been seperated in tags.
#
# Most commun use is for addition, convertion or removal of tags.
#
# Paramter: $tag_name         Name of the tag (before the :)
#           $tag_value        Value of the tag (after the :)
#           $linetype         Type for the courent file
#           $file_for_error   Name of the courent file
#           $line_for_error   Number of the courent line

sub additionnal_tag_parsing {
    my ( $tag_name, $tag_value, $linetype, $file_for_error, $line_for_error ) = @_;


    ##################################################################
    # [ 1398237 ] ALL: Convert Willpower to Will
    #
    # The BONUS:CHECKS and PRECHECKBASE tags must be converted
    #
    # BONUS:CHECKS|<list of save types>|<other tag parameters>
    # PRECHECKBASE:<number>,<list of saves>

    if ( $conversion_enable{'ALL:Willpower to Will'} ) {
        if ( $tag_name eq 'BONUS:CHECKS' ) {
            # We split the tag parameters
            my @tag_params = split q{\|}, $tag_value;


            # The Willpower keyword must be replace only in parameter 1
            # (parameter 0 is empty since the tag_value begins by | )
            if ( $tag_params[1] =~ s{ \b Willpower \b }{Will}xmsg ) {
                # We plug the new value in the calling parameter
                $_[1] = join q{|}, @tag_params;

                ewarn( WARNING,
                    qq{Replacing "$tag_name$tag_value" by "$_[0]$_[1]"},
                    $file_for_error,
                    $line_for_error
                );

            }

        }
        elsif ( $tag_name eq 'PRECHECKBASE' ){
            # Since the first parameter is a number, no need to
            # split before replacing.

            # Yes, we change directly the calling parameter
            if ( $_[1] =~ s{ \b Willpower \b }{Will}xmsg ) {
                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
    }


    ##################################################################
    # We find the tags that use the word Willpower

    if ( $conversion_enable{'ALL:Find Willpower'} && $cl_options{exportlist} ) {
        if ( $tag_value
                =~ m{ \b            # Word boundary
                      Willpower     # We need to find the word Willpower
                      \b            # Word boundary
                   }xmsi
        ) {
            # We write the tag and related information to the willpower.csv file
            my $tag_separator = $tag_name =~ / : /xms ? q{} : q{:};
            my $file_name = $file_for_error;
            $file_name =~ tr{/}{\\};
            print { $filehandle_for{Willpower} }
                qq{"$tag_name$tag_separator$tag_value","$line_for_error","$file_name"\n};
        }
    }

    ##################################################################
    # PRERACE now only accepts the format PRERACE:<number>,<race list>
    # All the PRERACE tags must be reformated to use the default way.

    if ( $conversion_enable{'ALL:PRERACE needs a ,'} ) {
        if ( $tag_name eq 'PRERACE' || $tag_name eq '!PRERACE' ) {
            if ( $tag_value !~ / \A \d+ [,], /xms ) {
                $_[1] = '1,' . $_[1];
                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif ( index( $tag_name, 'BONUS' ) == 0 && $tag_value =~ /PRERACE:([^]|]*)/ ) {
            my $prerace_value = $1;
            if ( $prerace_value !~ / \A \d+ [,] /xms ) {

                # There is no ',', we need to add one
                $_[1] =~ s/ PRERACE: (?!\d) /PRERACE:1,/xmsg;

                ewarn( WARNING,
                    qq{Replacing "$tag_name$tag_value" by "$_[0]$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif ( ( $tag_name eq 'SA' || $tag_name eq 'PREMULT' )
            && $tag_value =~ / PRERACE: ( [^]|]* ) /xms
        ) {
            my $prerace_value = $1;
            if ( $prerace_value !~ / \A \d+ [,] /xms ) {

                # There is no ',', we need to add one
                $_[1] =~ s/ PRERACE: (?!\d) /PRERACE:1,/xmsg;

                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
    }
    ##################################################################
    # [ 1173567 ] Convert old style PREALIGN to new style
    # PREALIGN now accept letters instead of numbers to specify alignments
    # All the PREALIGN tags must be reformated the letters.

    if ( $conversion_enable{'ALL:PREALING conversion'} ) {
        if ( $tag_name eq 'PREALIGN' || $tag_name eq '!PREALIGN' ) {
            my $new_value = join ',', map { $PREALIGN_conversion_5715{$_} || $_ } split ',',
                $tag_value;

            if ( $tag_value ne $new_value ) {
                $_[1] = $new_value;
                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif (index( $tag_name, 'BONUS' ) == 0
            || $tag_name eq 'SA'
            || $tag_name eq 'PREMULT' )
        {
            while ( $tag_value =~ /PREALIGN:([^]|]*)/g ) {
                my $old_value = $1;
                my $new_value = join ',', map { $PREALIGN_conversion_5715{$_} || $_ } split ',',
                    $old_value;

                if ( $new_value ne $old_value ) {

                    # There is no ',', we need to add one
                    $_[1] =~ s/PREALIGN:$old_value/PREALIGN:$new_value/;
                }
            }

            ewarn( WARNING,
                qq{Replacing "$tag_name$tag_value" by "$_[0]$_[1]"},
                $file_for_error,
                $line_for_error
            ) if $_[1] ne $tag_value;
        }
    }

    ##################################################################
    # [ 1070344 ] HITDICESIZE to HITDIE in templates.lst
    #
    # HITDICESIZE:.* must become HITDIE:.* in the TEMPLATE line types.

    if (   $conversion_enable{'TEMPLATE:HITDICESIZE to HITDIE'}
        && $tag_name eq 'HITDICESIZE'
        && $linetype eq 'TEMPLATE'
    ) {
        # We just change the tag name, the value remains the same.
        $_[0] = 'HITDIE';
        ewarn( WARNING,
            qq{Changing "$tag_name:$tag_value" to "$_[0]:$_[1]"},
            $file_for_error,
            $line_for_error
        );
    }

    ##################################################################
    # Remove all the PREALIGN tag from within BONUS, SA and
    # VFEAT tags.
    #
    # This is needed by my CMP friends .

    if ( $conversion_enable{'ALL:CMP remove PREALIGN'} ) {
        if ( $tag_value =~ /PREALIGN/ ) {
            my $ponc = $tag_name =~ /:/ ? "" : ":";

            if ( $tag_value =~ /PREMULT/ ) {
                ewarn( WARNING,
                    qq(PREALIGN found in PREMULT, you will have to remove it yourself "$tag_name$ponc$tag_value"),
                    $file_for_error,
                    $line_for_error
                );
            }
            elsif ( $tag_name =~ /^BONUS/ || $tag_name eq 'SA' || $tag_name eq 'VFEAT' ) {
                $_[1] = join( '|', grep { !/^(!?)PREALIGN/ } split '\|', $tag_value );
                ewarn( WARNING,
                    qq{Replacing "$tag_name$ponc$tag_value" with "$_[0]$ponc$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
            else {
                ewarn( WARNING,
                    qq(Found PREALIGN were I wasn't expecting it "$tag_name$ponc$tag_value"),
                    $file_for_error,
                    $line_for_error
                );
            }
        }
    }

    ##################################################################
    # [ 1006285 ] Convertion MOVE:<number> to MOVE:Walk,<Number>
    #
    # All the MOVE:<number> tags must be converted to
    # MOVE:Walk,<number>

    if (   $conversion_enable{'ALL:MOVE:nn to MOVE:Walk,nn'}
        && $tag_name eq "MOVE"
    ) {
        if ( $tag_value =~ /^(\d+$)/ ) {
            $_[1] = "Walk,$1";
            ewarn( WARNING,
                qq{Changing "$tag_name:$tag_value" to "$_[0]:$_[1]"},
                $file_for_error,
                $line_for_error
            );
        }
    }

    ##################################################################
    # [ 892746 ] KEYS entries were changed in the main files
    #
    # All the EQMOD and PRETYPE:EQMOD tags must be scanned for
    # possible KEY replacement.

#  if($conversion_enable{'ALL:EQMOD has new keys'} &&
#     ($tag_name eq "EQMOD" || $tag_name eq "REPLACES" || ($tag_name eq "PRETYPE" && $tag_value =~ /^(\d+,)?EQMOD/)))
#  {
#    for my $old_key (keys %Key_conversion_56)
#    {
#      if($tag_value =~ /\Q$old_key\E/)
#      {
#        @_[1] =~ s/\Q$old_key\E/$Key_conversion_56{$old_key}/;
#        ewarn( NOTICE,  qq(=> Replacing "$old_key" by "$Key_conversion_56{$old_key}" in "$tag_name:$tag_value"),
#          $file_for_error,$line_for_error );
#      }
#    }
#  }

    ##################################################################
    # [ 831569 ] RACE:CSKILL to MONCSKILL
    #
    # In the RACE files, all the CSKILL must be replaced with MONCSKILL
    # but only if MONSTERCLASS is present and there is not already a
    # MONCSKILL present.

    if (   $conversion_enable{'RACE:CSKILL to MONCSKILL'}
        && $linetype eq "RACE"
        && $tag_name eq "CSKILL"
    ) {
        ewarn( WARNING,
            qq{Found CSKILL in RACE file},
            $file_for_error,
            $line_for_error
        );
    }

    ##################################################################
    # GAMEMODE DnD is now 3e

    if (   $conversion_enable{'PCC:GAMEMODE DnD to 3e'}
        && $tag_name  eq "GAMEMODE"
        && $tag_value eq "DnD"
    ) {
        $_[1] = "3e";
        ewarn( WARNING,
            qq{Changing "$tag_name:$tag_value" to "$_[0]:$_[1]"},
            $file_for_error,
            $line_for_error
        );
    }

    ##################################################################
    # Add 3e to GAMEMODE:DnD_v30e and 35e to GAMEMODE:DnD_v35e

    if (   $conversion_enable{'PCC:GAMEMODE Add to the CMP DnD_'}
        && $tag_name eq "GAMEMODE"
        && $tag_value =~ /DnD_/
    ) {
        my ( $has_3e, $has_35e, $has_DnD_v30e, $has_DnD_v35e );

#        map {
#            $has_3e = 1
#                if $_ eq "3e";
#            $has_DnD_v30e = 1 if $_ eq "DnD_v30e";
#            $has_35e      = 1 if $_ eq "35e";
#            $has_DnD_v35e = 1 if $_ eq "DnD_v35e";
#        } split '\|', $tag_value;

        for my $game_mode (split q{\|}, $tag_value) {
            $has_3e       = 1 if $_ eq "3e";
            $has_DnD_v30e = 1 if $_ eq "DnD_v30e";
            $has_35e      = 1 if $_ eq "35e";
            $has_DnD_v35e = 1 if $_ eq "DnD_v35e";
        }

        $_[1] =~ s/(DnD_v30e)/3e\|$1/  if !$has_3e  && $has_DnD_v30e;
        $_[1] =~ s/(DnD_v35e)/35e\|$1/ if !$has_35e && $has_DnD_v35e;

        #$_[1] =~ s/(DnD_v30e)\|(3e)/$2\|$1/;
        #$_[1] =~ s/(DnD_v35e)\|(35e)/$2\|$1/;
        ewarn( WARNING,
            qq{Changing "$tag_name:$tag_value" to "$_[0]:$_[1]"},
            $file_for_error,
            $line_for_error
        ) if "$tag_name:$tag_value" ne "$_[0]:$_[1]";
    }

    ##################################################################
    # [ 784363 ] Add TYPE=Base.REPLACE to most BONUS:COMBAT|BAB
    # The BONUS:COMBAT|BAB found in CLASS, CLASS Level,
    # SUBCLASS and SUBCLASSLEVEL lines must have a |TYPE=Base.REPLACE added to them.
    # The same BONUSes found in RACE files with PREDEFAULTMONSTER tags
    # must also have the TYPE added.
    # All the other BONUS:COMBAT|BAB should be reported since there
    # should not be any really.

    if (   $conversion_enable{'ALL:Add TYPE=Base.REPLACE'}
        && $tag_name eq "BONUS:COMBAT"
        && $tag_value =~ /^\|(BAB)\|/i
    ) {

        # Is the BAB in uppercase ?
        if ( $1 ne 'BAB' ) {
            $_[1] =~ s/\|bab\|/\|BAB\|/i;
            ewarn( WARNING,
                qq{Changing "$tag_name$tag_value" to "$_[0]$_[1]" (BAB must be in uppercase)},
                $file_for_error,
                $line_for_error
            );
            $tag_value = $_[1];
        }

        # Is there already a TYPE= in the tag?
        my $is_type = $tag_value =~ /TYPE=/;

        # Is it the good one?
        my $is_type_base = $is_type && $tag_value =~ /TYPE=Base/;

        # Is there a .REPLACE at after the TYPE=Base?
        my $is_type_replace = $is_type_base && $tag_value =~ /TYPE=Base\.REPLACE/;

        # Is there a PREDEFAULTMONSTER tag embedded?
        my $is_predefaultmonster = $tag_value =~ /PREDEFAULTMONSTER/;

        # We must replace the CLASS, CLASS Level, SUBCLASS, SUBCLASSLEVEL
        # and PREDEFAULTMONSTER RACE lines
        if (   $linetype eq 'CLASS'
            || $linetype eq 'CLASS Level'
            || $linetype eq 'SUBCLASS'
            || $linetype eq 'SUBCLASSLEVEL'
            || ( ( $linetype eq 'RACE' || $linetype eq 'TEMPLATE' ) && $is_predefaultmonster ) )
        {
            if ( !$is_type ) {

                # We add the TYPE= statement at the end
                $_[1] .= '|TYPE=Base.REPLACE';
                ewarn( WARNING,
                    qq{Adding "|TYPE=Base.REPLACE" to "$tag_name$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }
            else {

                # The TYPE is already there but is it the correct one?
                if ( !$is_type_replace && $is_type_base ) {

                    # We add the .REPLACE part
                    $_[1] =~ s/\|TYPE=Base/\|TYPE=Base.REPLACE/;
                    ewarn( WARNING,
                        qq{Adding ".REPLACE" to "$tag_name$tag_value"},
                        $file_for_error,
                        $line_for_error
                    );
                }
                elsif ( !$is_type_base ) {
                    ewarn( INFO,
                        qq{Verify the TYPE of "$tag_name$tag_value"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
        }
        else {

            # If there is a BONUS:COMBAT elsewhere, we report it for manual
            # inspection.
            ewarn(INFO, qq{Verify this tag "$tag_name$tag_value"}, $file_for_error, $line_for_error);
        }
    }

    ##################################################################
    # [ 737718 ] COUNT[FEATTYPE] data change
    # A ALL. must be added at the end of every COUNT[FEATTYPE=FooBar]
    # found in the DEFINE tags if not already there.

    if (   $conversion_enable{'ALL:COUNT[FEATTYPE=...'}
        && $tag_name eq "DEFINE"
    ) {
        if ( $tag_value =~ /COUNT\[FEATTYPE=/i ) {
            my $value = $tag_value;
            my $new_value;
            while ( $value =~ /(.*?COUNT\[FEATTYPE=)([^\]]*)(\].*)/i ) {
                $new_value .= $1;
                my $count_value = $2;
                my $remaining   = $3;

                # We found a COUNT[FEATTYPE=, let's see if there is already
                # a ALL keyword in it.
                if ( $count_value !~ /^ALL\.|\.ALL\.|\.ALL$/i ) {
                    $count_value = 'ALL.' . $count_value;
                }

                $new_value .= $count_value;
                $value = $remaining;

            }
            $new_value .= $value;

            if ( $new_value ne $tag_value ) {
                $_[1] = $new_value;
                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
    }

    ##################################################################
    # PRECLASS now only accepts the format PRECLASS:1,<class>=<n>
    # All the PRECLASS tags must be reformated to use the default way.

    if ( $conversion_enable{'ALL:PRECLASS needs a ,'} ) {
        if ( $tag_name eq 'PRECLASS' || $tag_name eq '!PRECLASS' ) {
            unless ( $tag_value =~ /^\d+,/ ) {
                $_[1] = '1,' . $_[1];
                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif ( index( $tag_name, 'BONUS' ) == 0 && $tag_value =~ /PRECLASS:([^]|]*)/ ) {
            my $preclass_value = $1;
            unless ( $preclass_value =~ /^\d+,/ ) {

                # There is no ',', we need to add one
                $_[1] =~ s/PRECLASS:(?!\d)/PRECLASS:1,/g;

                ewarn( WARNING,
                    qq{Replacing "$tag_name$tag_value" by "$_[0]$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
        elsif ( ( $tag_name eq 'SA' || $tag_name eq 'PREMULT' )
            && $tag_value =~ /PRECLASS:([^]|]*)/
        ) {
            my $preclass_value = $1;
            unless ( $preclass_value =~ /^\d+,/ ) {

                # There is no ',', we need to add one
                $_[1] =~ s/PRECLASS:(?!\d)/PRECLASS:1,/g;

                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
    }

    ##################################################################
    # [ 711565 ] BONUS:MOVE replaced with BONUS:MOVEADD
    #
    # BONUS:MOVE must be replaced by BONUS:MOVEADD in all line types
    # except EQUIPMENT and EQUIPMOD where it most be replaced by
    # BONUS:POSTMOVEADD

    if (   $conversion_enable{'ALL:BONUS:MOVE convertion'}
        && $tag_name eq 'BONUS:MOVE'
    ) {
        if ( $linetype eq "EQUIPMENT" || $linetype eq "EQUIPMOD" ) {
            $_[0] = "BONUS:POSTMOVEADD";
        }
        else {
            $_[0] = "BONUS:MOVEADD";
        }

        ewarn( WARNING,
            qq{Replacing "$tag_name$tag_value" by "$_[0]$_[1]"},
            $file_for_error,
            $line_for_error
        );
    }

    ##################################################################
    # [ 699834 ] Incorrect loading of multiple vision types
    # All the , in the VISION tags must be converted to | except for the
    # VISION:.ADD (these will be converted later to BONUS:VISION)
    #
    # [ 728038 ] BONUS:VISION must replace VISION:.ADD
    # Now doing the VISION:.ADD convertion

    if (   $conversion_enable{'ALL: , to | in VISION'}
        && $tag_name eq 'VISION'
    ) {
        unless ( $tag_value =~ /(\.ADD,|1,)/i ) {
            if ( $_[1] =~ tr{,}{|} ) {
                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
    }

    ##################################################################
    # PRESTAT now only accepts the format PRESTAT:1,<stat>=<n>
    # All the PRESTAT tags must be reformated to use the default way.

    if (   $conversion_enable{'ALL:PRESTAT needs a ,'}
        && $tag_name eq 'PRESTAT'
    ) {
        if ( index( $tag_value, ',' ) == -1 ) {

            # There is no ',', we need to add one
            $_[1] = '1,' . $_[1];
            ewarn( WARNING,
                qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                $file_for_error,
                $line_for_error
            );
        }
    }

    ##################################################################
    # [ 686169 ] remove ATTACKS: tag
    # ATTACKS:<attacks> must be replaced by BONUS:COMBAT|ATTACKS|<attacks>

    if (   $conversion_enable{'EQUIPMENT: remove ATTACKS'}
        && $tag_name eq 'ATTACKS'
        && $linetype eq 'EQUIPMENT'
    ) {
        my $number_attacks = $tag_value;
        $_[0] = 'BONUS:COMBAT';
        $_[1] = '|ATTACKS|' . $number_attacks;

        ewarn( WARNING,
            qq{Replacing "$tag_name:$tag_value" by "$_[0]$_[1]"},
            $file_for_error,
            $line_for_error
        );
    }

    ##################################################################
    # Name change for SRD compliance (PCGEN 4.3.3)

    if ($conversion_enable{'ALL: 4.3.3 Weapon name change'}
        && (   $tag_name eq 'WEAPONBONUS'
            || $tag_name eq 'WEAPONAUTO'
            || $tag_name eq 'PROF'
            || $tag_name eq 'GEAR'
            || $tag_name eq 'FEAT'
            || $tag_name eq 'PROFICIENCY'
            || $tag_name eq 'DEITYWEAP'
            || $tag_name eq 'MFEAT' )
    ) {
        for ( keys %srd_weapon_name_convertion_433 ) {
            if ( $_[1] =~ s/\Q$_\E/$srd_weapon_name_convertion_433{$_}/ig ) {
                ewarn( WARNING,
                    qq{Replacing "$tag_name:$tag_value" by "$_[0]:$_[1]"},
                    $file_for_error,
                    $line_for_error
                );
            }
        }
    }
}

###############################################################
# validate_line
# -------------
#
# This function perform validation that must be done on a
# whole line at a time.
#
# Paramter: $line_ref         Ref to a hash containing the tags of the line
#           $linetype         Type for the courent line
#           $file_for_error   Name of the courent file
#           $line_for_error   Number of the courent line

sub validate_line {
    my ( $line_ref, $linetype, $file_for_error, $line_for_error ) = @_;

    ########################################################
    # Validation for the line identifier
    ########################################################

    if ( !($linetype eq 'SOURCE'
        || $linetype eq 'KIT LANGAUTO'
        || $linetype eq 'KIT NAME'
        || $file_for_error =~ m{ [.] PCC \z }xmsi
        || $linetype eq 'COMPANIONMOD') # FOLLOWER:Class1,Class2=level
    ) {

        # We get the line identifier.
        my $identifier = $line_ref->{ $master_order{$linetype}[0] }[0];

        # We hunt for the bad coma.
        if($identifier =~ /,/) {
            ewarn( NOTICE,
                qq{"," (coma) should not be used in line identifier name: $identifier},
                $file_for_error,
                $line_for_error
            );
        }
    }

    ########################################################
    # Special validation for specific tags
    ########################################################

    if ( 0 && $linetype eq 'SPELL' )    # disabled for now.
    {

        # Either or both CLASSES and DOMAINS tags must be
        # present in a normal SPELL line

        if (   exists $line_ref->{'000SpellName'}
            && $line_ref->{'000SpellName'}[0] !~ /\.MOD$/
            && exists $line_ref->{'TYPE'}
            && $line_ref->{'TYPE'}[0] ne 'TYPE:Psionic.Attack Mode'
            && $line_ref->{'TYPE'}[0] ne 'TYPE:Psionic.Defense Mode' )
        {
            ewarn(INFO,
                  qq(No CLASSES or DOMAINS tag found for SPELL "$line_ref->{'000SpellName'}[0]"),
                  $file_for_error,
                  $line_for_error
            ) if !( exists $line_ref->{'CLASSES'} || exists $line_ref->{'DOMAINS'} );
        }
    }
    elsif ( $linetype eq "FEAT" ) {

        # On a FEAT line type:
        # 1) if it has MULT:YES, it  _has_ to have CHOOSE
        # 2) if it has CHOOSE, it _has_ to have MULT:YES
        # 3) if it has STACK:YES, it _has_ to have MULT:YES (and CHOOSE)
        my ( $hasCHOOSE, $hasMULT, $hasSTACK );

        $hasCHOOSE = 1 if exists $line_ref->{'CHOOSE'};
        $hasMULT   = 1 if exists $line_ref->{'MULT'} && $line_ref->{'MULT'}[0] =~ /^MULT:Y/i;
        $hasSTACK  = 1 if exists $line_ref->{'STACK'} && $line_ref->{'STACK'}[0] =~ /^STACK:Y/i;

        if ( $hasMULT && !$hasCHOOSE ) {
            ewarn(INFO,
                  qq(The CHOOSE tag is mandantory when MULT:YES is present in FEAT "$line_ref->{'000FeatName'}[0]"),
                  $file_for_error,
                  $line_for_error
            );
        }
        elsif ( $hasCHOOSE && !$hasMULT && $line_ref->{'CHOOSE'}[0] !~ /CHOOSE:SPELLLEVEL/i ) {

            # The CHOOSE:SPELLLEVEL is exampted from this particular rule.
            ewarn(INFO,
                  qq(The MULT:YES tag is mandantory when CHOOSE is present in FEAT "$line_ref->{'000FeatName'}[0]"),
                  $file_for_error,
                  $line_for_error
            );
        }

        if ( $hasSTACK && !$hasMULT ) {
            ewarn(INFO,
                  qq(The MULT:YES tag is mandantory when STACK:YES is present in FEAT "$line_ref->{'000FeatName'}[0]"),
                  $file_for_error,
                  $line_for_error
            );
        }

        # We identify the feats that can have sub-entities. e.g. Spell Focus(Spellcraft)
        if ($hasCHOOSE) {

            # The CHOSE type tells us the type of sub-entities
            my $choose    = $line_ref->{'CHOOSE'}[0];
            my $feat_name = $line_ref->{'000FeatName'}[0];
            $feat_name =~ s/.MOD$//;

            if ( $choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?(FEAT=[^|]*)/ ) {
                $valid_sub_entities{'FEAT'}{$feat_name} = $1;
            }
            elsif ( $choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?FEATLIST/ ) {
                $valid_sub_entities{'FEAT'}{$feat_name} = 'FEAT';
            }
            elsif ( $choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?(?:WEAPONPROFS|Exotic|Martial)/ ) {
                $valid_sub_entities{'FEAT'}{$feat_name} = 'WEAPONPROF';
            }
            elsif ( $choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?SKILLSNAMED/ ) {
                $valid_sub_entities{'FEAT'}{$feat_name} = 'SKILL';
            }
            elsif ( $choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?SCHOOLS/ ) {
                $valid_sub_entities{'FEAT'}{$feat_name} = 'SPELL_SCHOOL';
            }
            elsif ( $choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?SPELLLIST/ ) {
                $valid_sub_entities{'FEAT'}{$feat_name} = 'SPELL';
            }
            elsif ($choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?SPELLLEVEL/
                || $choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?HP/ )
            {

                # Ad-Lib is a special case that means "Don't look for
                # anything else".
                $valid_sub_entities{'FEAT'}{$feat_name} = 'Ad-Lib';
            }
            elsif ( $choose =~ /^CHOOSE:(?:COUNT=\d+\|)?(.*)/ ) {

                # ad-hod/special list of thingy
                # It adds to the valid entities instead of the
                # valid sub-entities.
                # We do this when we find a CHOOSE but we do not
                # know what it is for.
                for my $sub_type ( split '\|', $1 ) {
                    $valid_entities{'FEAT'}{"$feat_name($sub_type)"}  = $1;
                    $valid_entities{'FEAT'}{"$feat_name ($sub_type)"} = $1;
                }
            }
        }
    }
    elsif ( $linetype eq "EQUIPMOD" ) {

        # We keep track of the KEYs for the equipmods.
        if ( exists $line_ref->{'KEY'} ) {

            # The KEY tag should only have one value and there should always be only
            # one KEY tag by EQUIPMOD line.

            # We extract the key name
            my ($key) = ( $line_ref->{'KEY'}[0] =~ /KEY:(.*)/ );

            if ($key) {
                $valid_entities{"EQUIPMOD Key"}{$key}++;
            }
            else {
                ewarn(WARNING,
                      qq(Could not parse the KEY in "$line_ref->{'KEY'}[0]"),
                      $file_for_error,
                      $line_for_error
                );
            }
        }
        else {
            ewarn(INFO,
                  qq(No KEY tag found for "$line_ref->{$column_with_no_tag{'EQUIPMOD'}[0]}[0]"),
                  $file_for_error,
                  $line_for_error
            );
        }
    }
    elsif ( $linetype eq "CLASS" ) {

        # [ 876536 ] All spell casting classes need CASTERLEVEL
        #
        # If SPELLTYPE is present and BONUS:CASTERLEVEL is not present,
        # we warn the user.

        if ( exists $line_ref->{'SPELLTYPE'} && !exists $line_ref->{'BONUS:CASTERLEVEL'} ) {
            ewarn( INFO,
                qq{Missing BONUS:CASTERLEVEL for "$line_ref->{$column_with_no_tag{'CLASS'}[0]}[0]"},
                $file_for_error,
                $line_for_error
            );
        }
    }
    elsif ( $linetype eq 'SKILL' ) {

        # We must identify the skills that have sub-entity e.g. Speak Language (Infernal)

        if ( exists $line_ref->{'CHOOSE'} ) {

            # The CHOSE type tells us the type of sub-entities
            my $choose     = $line_ref->{'CHOOSE'}[0];
            my $skill_name = $line_ref->{'000SkillName'}[0];
            $skill_name =~ s/.MOD$//;

            if ( $choose =~ /^CHOOSE:(?:NUMCHOICES=\d+\|)?Language/ ) {
                $valid_sub_entities{'SKILL'}{$skill_name} = 'LANGUAGE';
            }
        }
    }
}

###############################################################
# additionnal_line_parsing
# ------------------------
#
# This function does additional parsing on each line once
# they have been seperated in tags.
#
# Most commun use is for addition, convertion or removal of tags.
#
# Paramter: $line_ref         Ref to a hash containing the tags of the line
#           $filetype         Type for the courent file
#           $file_for_error   Name of the courent file
#           $line_for_error   Number of the courent line
#           $line_info        (Optional) structure generated by FILETYPE_parse
#

BEGIN {

    my $class_name = "";

    sub additionnal_line_parsing {
        my ( $line_ref, $filetype, $file_for_error, $line_for_error, $line_info ) = @_;

        ##################################################################
        # [ 1444527 ] New SOURCE tag format
        #
        # The SOURCELONG tags found on any linetype but the SOURCE line type must
        # be converted to use tab if | are found.

        if (   $conversion_enable{'ALL:New SOURCExxx tag format'}
            && exists $line_ref->{'SOURCELONG'} ) {
            my @new_tags;

            for my $tag ( @{ $line_ref->{'SOURCELONG'} } ) {
                if( $tag =~ / [|] /xms ) {
                    push @new_tags, split '\|', $tag;
                    ewarn( WARNING,
                        qq{Spliting "$tag"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }

            if( @new_tags ) {
                delete $line_ref->{'SOURCELONG'};

                for my $new_tag (@new_tags) {
                    my ($tag_name) = ( $new_tag =~ / ( [^:]* ) [:] /xms );
                    push @{ $line_ref->{$tag_name} }, $new_tag;
                }
            }
        }

        ##################################################################
        # [ 1070084 ] Convert SPELL to SPELLS
        #
        # Convert the old SPELL tags to the new SPELLS format.
        #
        # Old SPELL:<spellname>|<nb per day>|<spellbook>|...|PRExxx|PRExxx|...
        # New SPELLS:<spellbook>|TIMES=<nb per day>|<spellname>|<spellname>|PRExxx...

        if ( $conversion_enable{'ALL:Convert SPELL to SPELLS'}
            && exists $line_ref->{'SPELL'} )
        {
            my %spellbooks;

            # We parse all the existing SPELL tags
            for my $tag ( @{ $line_ref->{'SPELL'} } ) {
                my ( $tag_name, $tag_value ) = ( $tag =~ /^([^:]*):(.*)/ );
                my @elements = split '\|', $tag_value;
                my @pretags;

                while ( $elements[ +@elements - 1 ] =~ /^!?PRE\w*:/ ) {

                    # We keep the PRE tags separated
                    unshift @pretags, pop @elements;
                }

                # We classify each triple <spellname>|<nb per day>|<spellbook>
                while (@elements) {
                    if ( +@elements < 3 ) {
                        ewarn(WARNING,
                              qq(Wrong number of elements for "$tag_name:$tag_value"),
                              $file_for_error,
                              $line_for_error
                        );
                    }

                    my $spellname = shift @elements;
                    my $times     = +@elements ? shift @elements : 99999;
                    my $pretags   = join '|', @pretags;
                    $pretags = "NONE" unless $pretags;
                    my $spellbook = +@elements ? shift @elements : "MISSING SPELLBOOK";

                    push @{ $spellbooks{$spellbook}{$times}{$pretags} }, $spellname;
                }

                ewarn( WARNING,
                    qq{Removing "$tag_name:$tag_value"},
                    $file_for_error,
                    $line_for_error
                );
            }

            # We delete the SPELL tags
            delete $line_ref->{'SPELL'};

            # We add the new SPELLS tags
            for my $spellbook ( sort keys %spellbooks ) {
                for my $times ( sort keys %{ $spellbooks{$spellbook} } ) {
                    for my $pretags ( sort keys %{ $spellbooks{$spellbook}{$times} } ) {
                        my $spells = "SPELLS:$spellbook|TIMES=$times";

                        for my $spellname ( sort @{ $spellbooks{$spellbook}{$times}{$pretags} } ) {
                            $spells .= "|$spellname";
                        }

                        $spells .= "|$pretags" unless $pretags eq "NONE";

                        ewarn( WARNING, qq{Adding   "$spells"}, $file_for_error, $line_for_error );

                        push @{ $line_ref->{'SPELLS'} }, $spells;
                    }
                }
            }
        }

        ##################################################################
        # We get rid of all the PREALIGN tags.
        #
        # This is needed by my good CMP friends.

        if ( $conversion_enable{'ALL:CMP remove PREALIGN'} ) {
            if ( exists $line_ref->{'PREALIGN'} ) {
                my $number = +@{ $line_ref->{'PREALIGN'} };
                delete $line_ref->{'PREALIGN'};
                ewarn( WARNING,
                    qq{Removing $number PREALIGN tags},
                    $file_for_error,
                    $line_for_error
                );
            }

            if ( exists $line_ref->{'!PREALIGN'} ) {
                my $number = +@{ $line_ref->{'!PREALIGN'} };
                delete $line_ref->{'!PREALIGN'};
                ewarn( WARNING,
                    qq{Removing $number !PREALIGN tags},
                    $file_for_error,
                    $line_for_error
                );
            }
        }

        ##################################################################
        # Need to fix the STR bonus when the monster have only one
        # Natural Attack (STR bonus is then 1.5 * STR).
        # We add it if there is only one Melee attack and the
        # bonus is not already present.

        if ( $conversion_enable{'ALL:CMP NatAttack fix'}
            && exists $line_ref->{'NATURALATTACKS'} )
        {

            # First we verify if if there is only one melee attack.
            if ( @{ $line_ref->{'NATURALATTACKS'} } == 1 ) {
                my @NatAttacks = split '\|', $line_ref->{'NATURALATTACKS'}[0];
                if ( @NatAttacks == 1 ) {
                    my ( $NatAttackName, $Types, $NbAttacks, $Damage ) = split ',', $NatAttacks[0];
                    if ( $NbAttacks eq '*1' && $Damage ) {

                        # Now, at last, we know there is only one Natural Attack
                        # Is it a Melee attack?
                        my @Types    = split '\.', $Types;
                        my $IsMelee  = 0;
                        my $IsRanged = 0;
                        for my $type (@Types) {
                            $IsMelee  = 1 if uc($type) eq 'MELEE';
                            $IsRanged = 1 if uc($type) eq 'RANGED';
                        }

                        if ( $IsMelee && !$IsRanged ) {

                            # We have a winner!!!
                            ($NatAttackName) = ( $NatAttackName =~ /:(.*)/ );

                            # Well, maybe the BONUS:WEAPONPROF is already there.
                            if ( exists $line_ref->{'BONUS:WEAPONPROF'} ) {
                                my $AlreadyThere = 0;
                                FIND_BONUS:
                                for my $bonus ( @{ $line_ref->{'BONUS:WEAPONPROF'} } ) {
                                    if ( $bonus eq "BONUS:WEAPONPROF=$NatAttackName|DAMAGE|STR/2" )
                                    {
                                        $AlreadyThere = 1;
                                        last FIND_BONUS;
                                    }
                                }

                                unless ($AlreadyThere) {
                                    push @{ $line_ref->{'BONUS:WEAPONPROF'} },
                                        "BONUS:WEAPONPROF=$NatAttackName|DAMAGE|STR/2";
                                    ewarn( WARNING,
                                        qq{Added "$line_ref->{'BONUS:WEAPONPROF'}[0]"}
                                            . qq{ to go with "$line_ref->{'NATURALATTACKS'}[0]"},
                                        $file_for_error,
                                        $line_for_error
                                    );
                                }
                            }
                            else {
                                $line_ref->{'BONUS:WEAPONPROF'}
                                    = ["BONUS:WEAPONPROF=$NatAttackName|DAMAGE|STR/2"];
                                ewarn( WARNING,
                                    qq{Added "$line_ref->{'BONUS:WEAPONPROF'}[0]"}
                                        . qq{to go with "$line_ref->{'NATURALATTACKS'}[0]"},
                                    $file_for_error,
                                    $line_for_error
                                );
                            }
                        }
                        elsif ( $IsMelee && $IsRanged ) {
                            ewarn(WARNING,
                                qq{This natural attack is both Melee and Ranged}
                                    . qq{"$line_ref->{'NATURALATTACKS'}[0]"},
                                $file_for_error,
                                $line_for_error
                            );
                        }
                    }
                }
            }
        }

        ##################################################################
        # [ 865826 ] Remove the deprecated MOVE tag in EQUIPMENT files
        # No conversion needed. We just have to remove the MOVE tags that
        # are doing nothing anyway.

        if (   $conversion_enable{'EQUIP:no more MOVE'}
            && $filetype eq "EQUIPMENT"
            && exists $line_ref->{'MOVE'} )
        {
            ewarn( WARNING, qq{Removed MOVE tags}, $file_for_error, $line_for_error );
            delete $line_ref->{'MOVE'};
        }

        ##################################################################
        # Every RACE that has a Climb or a Swim MOVE must have a
        # BONUS:SLILL|Climb|8|TYPE=Racial. If there is a
        # BONUS:SKILLRANK|Swim|8|PREDEFAULTMONSTER:Y present, it must be
        # removed or lowered by 8.

        if (   $conversion_enable{'RACE:BONUS SKILL Climb and Swim'}
            && $filetype eq "RACE"
            && exists $line_ref->{'MOVE'} )
        {
            my $swim  = $line_ref->{'MOVE'}[0] =~ /swim/i;
            my $climb = $line_ref->{'MOVE'}[0] =~ /climb/i;

            if ( $swim || $climb ) {
                my $need_swim  = 1;
                my $need_climb = 1;

                # Is there already a BONUS:SKILL|Swim of at least 8 rank?
                if ( exists $line_ref->{'BONUS:SKILL'} ) {
                    for my $skill ( @{ $line_ref->{'BONUS:SKILL'} } ) {
                        if ( $skill =~ /^BONUS:SKILL\|([^|]*)\|(\d+)\|TYPE=Racial/i ) {
                            my $skill_list = $1;
                            my $skill_rank = $2;

                            $need_swim  = 0 if $skill_list =~ /swim/i;
                            $need_climb = 0 if $skill_list =~ /climb/i;

                            if ( $need_swim && $skill_rank == 8 ) {
                                $skill_list
                                    = join( ',', sort( split ( ',', $skill_list ), 'Swim' ) );
                                $skill = "BONUS:SKILL|$skill_list|8|TYPE=Racial";
                                ewarn( WARNING,
                                    qq{Added Swim to "$skill"},
                                    $file_for_error,
                                    $line_for_error
                                );
                            }

                            if ( $need_climb && $skill_rank == 8 ) {
                                $skill_list
                                    = join( ',', sort( split ( ',', $skill_list ), 'Climb' ) );
                                $skill = "BONUS:SKILL|$skill_list|8|TYPE=Racial";
                                ewarn( WARNING,
                                    qq{Added Climb to "$skill"},
                                    $file_for_error,
                                    $line_for_error
                                );
                            }

                            if ( ( $need_climb || $need_swim ) && $skill_rank != 8 ) {
                                ewarn( INFO,
                                    qq{You\'ll have to deal with this one yourself "$skill"},
                                    $file_for_error,
                                    $line_for_error
                                );
                            }
                        }
                    }
                }
                else {
                    $need_swim  = $swim;
                    $need_climb = $climb;
                }

                # Is there a BONUS:SKILLRANK to remove?
                if ( exists $line_ref->{'BONUS:SKILLRANK'} ) {
                    for ( my $index = 0; $index < @{ $line_ref->{'BONUS:SKILLRANK'} }; $index++ ) {
                        my $skillrank = $line_ref->{'BONUS:SKILLRANK'}[$index];

                        if ( $skillrank =~ /^BONUS:SKILLRANK\|(.*)\|(\d+)\|PREDEFAULTMONSTER:Y/ ) {
                            my $skill_list = $1;
                            my $skill_rank = $2;

                            if ( $climb && $skill_list =~ /climb/i ) {
                                if ( $skill_list eq "Climb" ) {
                                    $skill_rank -= 8;
                                    if ($skill_rank) {
                                        $skillrank
                                            = "BONUS:SKILLRANK|Climb|$skill_rank|PREDEFAULTMONSTER:Y";
                                        ewarn( WARNING,
                                            qq{Lowering skill rank in "$skillrank"},
                                            $file_for_error,
                                            $line_for_error
                                        );
                                    }
                                    else {
                                        ewarn( WARNING,
                                            qq{Removing "$skillrank"},
                                            $file_for_error,
                                            $line_for_error
                                        );
                                        delete $line_ref->{'BONUS:SKILLRANK'}[$index];
                                        $index--;
                                    }
                                }
                                else {
                                    ewarn( INFO,
                                        qq{You\'ll have to deal with this one yourself "$skillrank"},
                                        $file_for_error,
                                        $line_for_error
                                    );;
                                }
                            }

                            if ( $swim && $skill_list =~ /swim/i ) {
                                if ( $skill_list eq "Swim" ) {
                                    $skill_rank -= 8;
                                    if ($skill_rank) {
                                        $skillrank
                                            = "BONUS:SKILLRANK|Swim|$skill_rank|PREDEFAULTMONSTER:Y";
                                        ewarn( WARNING,
                                            qq{Lowering skill rank in "$skillrank"},
                                            $file_for_error,
                                            $line_for_error
                                        );
                                    }
                                    else {
                                        ewarn( WARNING,
                                            qq{Removing "$skillrank"},
                                            $file_for_error,
                                            $line_for_error
                                        );
                                        delete $line_ref->{'BONUS:SKILLRANK'}[$index];
                                        $index--;
                                    }
                                }
                                else {
                                    ewarn( INFO,
                                        qq{You\'ll have to deal with this one yourself "$skillrank"},
                                        $file_for_error,
                                        $line_for_error
                                    );
                                }
                            }
                        }
                    }

                    # If there are no more BONUS:SKILLRANK, we remove the tag entry
                    delete $line_ref->{'BONUS:SKILLRANK'}
                        unless @{ $line_ref->{'BONUS:SKILLRANK'} };
                }
            }
        }

        ##################################################################
        # [ 845853 ] SIZE is no longer valid in the weaponprof files
        #
        # The SIZE tag must be removed from all WEAPONPROF files since it
        # cause loading problems with the latest versio of PCGEN.

        if (   $conversion_enable{'WEAPONPROF:No more SIZE'}
            && $filetype eq "WEAPONPROF"
            && exists $line_ref->{'SIZE'} )
        {
            ewarn( WARNING,
                qq{Removing the SIZE tag in line "$line_ref->{$master_order{'WEAPONPROF'}[0]}[0]"},
                $file_for_error,
                $line_for_error
            );
            delete $line_ref->{'SIZE'};
        }

        ##################################################################
        # [ 832164 ] Adding NoProfReq to AUTO:WEAPONPROF for most races
        #
        # NoProfReq must be added to AUTO:WEAPONPROF if the race has
        # at least one hand and if NoProfReq is not already there.

        if (   $conversion_enable{'RACE:NoProfReq'}
            && $filetype eq "RACE" )
        {
            my $needNoProfReq = 1;

            # Is NoProfReq already present?
            if ( exists $line_ref->{'AUTO:WEAPONPROF'} ) {
                $needNoProfReq = 0 if $line_ref->{'AUTO:WEAPONPROF'}[0] =~ /NoProfReq/;
            }

            my $nbHands = 2;    # Default when no HANDS tag is present

            # How many hands?
            if ( exists $line_ref->{'HANDS'} ) {
                if ( $line_ref->{'HANDS'}[0] =~ /HANDS:(\d+)/ ) {
                    $nbHands = $1;
                }
                else {
                    ewarn(INFO,
                          qq(Invalid value in tag "$line_ref->{'HANDS'}[0]"),
                          $file_for_error,
                          $line_for_error
                    );
                    $needNoProfReq = 0;
                }
            }

            if ( $needNoProfReq && $nbHands ) {
                if ( exists $line_ref->{'AUTO:WEAPONPROF'} ) {
                    ewarn( WARNING,
                        qq{Adding "TYPE=NoProfReq" to tag "$line_ref->{'AUTO:WEAPONPROF'}[0]"},
                        $file_for_error,
                        $line_for_error
                    );
                    $line_ref->{'AUTO:WEAPONPROF'}[0] .= "|TYPE=NoProfReq";
                }
                else {
                    $line_ref->{'AUTO:WEAPONPROF'} = ["AUTO:WEAPONPROF|TYPE=NoProfReq"];
                    ewarn( WARNING,
                        qq{Creating new tag "AUTO:WEAPONPROF|TYPE=NoProfReq"},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
        }

        ##################################################################
        # [ 831569 ] RACE:CSKILL to MONCSKILL
        #
        # In the RACE files, all the CSKILL must be replaced with MONCSKILL
        # but only if MONSTERCLASS is present and there is not already a
        # MONCSKILL present.

        if (   $conversion_enable{'RACE:CSKILL to MONCSKILL'}
            && $filetype eq "RACE"
            && exists $line_ref->{'CSKILL'}
            && exists $line_ref->{'MONSTERCLASS'}
            && !exists $line_ref->{'MONCSKILL'} )
        {
            ewarn( WARNING,
                qq{Change CSKILL for MONSKILL in "$line_ref->{'CSKILL'}[0]"},
                $file_for_error,
                $line_for_error
            );

            $line_ref->{'MONCSKILL'} = [ "MON" . $line_ref->{'CSKILL'}[0] ];
            delete $line_ref->{'CSKILL'};
        }

        ##################################################################
        # [ 728038 ] BONUS:VISION must replace VISION:.ADD
        #
        # VISION:.ADD must be converted to BONUS:VISION
        # Some exemple of VISION:.ADD tags:
        #   VISION:.ADD,Darkvision (60')
        #   VISION:1,Darkvision (60')
        #   VISION:.ADD,See Invisibility (120'),See Etheral (120'),Darkvision (120')

        if (   $conversion_enable{'ALL: , to | in VISION'}
            && exists $line_ref->{'VISION'}
            && $line_ref->{'VISION'}[0] =~ /(\.ADD,|1,)(.*)/i )
        {
            ewarn( WARNING,
                qq{Removing "$line_ref->{'VISION'}[0]"},
                $file_for_error,
                $line_for_error
            );

            my $newvision = "VISION:";
            my $coma;

            for my $vision_bonus ( split ',', $2 ) {
                if ( $vision_bonus =~ /(\w+)\s*\((\d+)\'\)/ ) {
                    my ( $type, $bonus ) = ( $1, $2 );
                    push @{ $line_ref->{'BONUS:VISION'} }, "BONUS:VISION|$type|$bonus";
                    ewarn( WARNING,
                        qq{Adding "BONUS:VISION|$type|$bonus"},
                        $file_for_error,
                        $line_for_error
                    );
                    $newvision .= "$coma$type (0')";
                    $coma = ',';
                }
                else {
                    ewarn( ERROR,
                        qq(Do not know how to convert "VISION:.ADD,$vision_bonus"),
                        $file_for_error,
                        $line_for_error
                    );
                }
            }

            ewarn( WARNING, qq{Adding "$newvision"}, $file_for_error, $line_for_error );

            $line_ref->{'VISION'} = [$newvision];
        }

        ##################################################################
        #
        #
        # For items with TYPE:Boot, Glove, Bracer, we must check for plural
        # form and add a SLOTS:2 tag is the item is plural.

        if (   $conversion_enable{'EQUIPMENT: SLOTS:2 for plurals'}
            && $filetype       eq 'EQUIPMENT'
            && $line_info->[0] eq 'EQUIPMENT'
            && !exists $line_ref->{'SLOTS'} )
        {
            my $equipment_name = $line_ref->{ $master_order{'EQUIPMENT'}[0] }[0];

            if ( exists $line_ref->{'TYPE'} ) {
                my $type = $line_ref->{'TYPE'}[0];
                if ( $type =~ /(Boot|Glove|Bracer)/ ) {
                    if (   $1 eq 'Boot' && $equipment_name =~ /boots|sandals/i
                        || $1 eq 'Glove'  && $equipment_name =~ /gloves|gauntlets|straps/i
                        || $1 eq 'Bracer' && $equipment_name =~ /bracers|bracelets/i )
                    {
                        $line_ref->{'SLOTS'} = ['SLOTS:2'];
                        ewarn( WARNING,
                            qq{"SLOTS:2" added to "$equipment_name"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                    else {
                        ewarn( ERROR, qq{"$equipment_name" is a $1}, $file_for_error, $line_for_error );
                    }
                }
            }
            else {
                ewarn( WARNING,
                    qq{$equipment_name has no TYPE.},
                    $file_for_error,
                    $line_for_error
                ) unless $equipment_name =~ /.MOD$/i;
            }
        }

        ##################################################################
        # #[ 677962 ] The DMG wands have no charge.
        #
        # Any Wand that do not have a EQMOD tag most have one added.
        #
        # The syntax for the new tag is
        # EQMOD:SE_50TRIGGER|SPELLNAME[$spell_name]SPELLLEVEL[$spell_level]CASTERLEVEL[$caster_level]CHARGES[50]
        #
        # The $spell_level will also be extracted from the CLASSES tag.
        # The $caster_level will be $spell_level * 2 -1

        if ( $conversion_enable{'EQUIPMENT: generate EQMOD'} ) {
            if (   $filetype eq 'SPELL'
                && $line_info->[0] eq 'SPELL'
                && ( exists $line_ref->{'CLASSES'} ) )
            {
                my $spell_name  = $line_ref->{'000SpellName'}[0];
                my $spell_level = -1;

                CLASS:
                for ( split '\|', $line_ref->{'CLASSES'}[0] ) {
                    if ( index( $_, 'Wizard' ) != -1 || index( $_, 'Cleric' ) != -1 ) {
                        $spell_level = (/=(\d+)$/)[0];
                        last CLASS;
                    }
                }

                $Spells_For_EQMOD{$spell_name} = $spell_level
                    if $spell_level > -1;

            }
            elsif ($filetype eq 'EQUIPMENT'
                && $line_info->[0] eq 'EQUIPMENT'
                && ( !exists $line_ref->{'EQMOD'} ) )
            {
                my $equip_name = $line_ref->{'000EquipmentName'}[0];
                my $spell_name;

                if ( $equip_name =~ m{^Wand \((.*)/(\d\d?)(st|rd|th) level caster\)} ) {
                    $spell_name = $1;
                    my $caster_level = $2;

                    if ( exists $Spells_For_EQMOD{$spell_name} ) {
                        my $spell_level = $Spells_For_EQMOD{$spell_name};
                        my $eqmod_tag   = "EQMOD:SE_50TRIGGER|SPELLNAME[$spell_name]"
                            . "SPELLLEVEL[$spell_level]"
                            . "CASTERLEVEL[$caster_level]CHARGES[50]";
                        $line_ref->{'EQMOD'}    = [$eqmod_tag];
                        $line_ref->{'BASEITEM'} = ['BASEITEM:Wand']
                            unless exists $line_ref->{'BASEITEM'};
                        delete $line_ref->{'COST'} if exists $line_ref->{'COST'};
                        ewarn( WARNING,
                            qq{$equip_name: removing "COST" and adding "$eqmod_tag"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                    else {
                        ewarn( WARNING,
                            qq($equip_name: not enough information to add charges),
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }
                elsif ( $equip_name =~ /^Wand \((.*)\)/ ) {
                    $spell_name = $1;
                    if ( exists $Spells_For_EQMOD{$spell_name} ) {
                        my $spell_level  = $Spells_For_EQMOD{$spell_name};
                        my $caster_level = $spell_level * 2 - 1;
                        my $eqmod_tag    = "EQMOD:SE_50TRIGGER|SPELLNAME[$spell_name]"
                            . "SPELLLEVEL[$spell_level]"
                            . "CASTERLEVEL[$caster_level]CHARGES[50]";
                        $line_ref->{'EQMOD'} = [$eqmod_tag];
                        delete $line_ref->{'COST'} if exists $line_ref->{'COST'};
                        ewarn( WARNING,
                            qq{$equip_name: removing "COST" and adding "$eqmod_tag"},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                    else {
                        ewarn( WARNING,
                            qq{$equip_name: not enough information to add charges},
                            $file_for_error,
                            $line_for_error
                        );
                    }
                }
                elsif ( $equip_name =~ /^Wand/ ) {
                    ewarn( WARNING,
                        qq{$equip_name: not enough information to add charges},
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
        }

        ##################################################################
        # [ 663491 ] RACE: Convert AGE, HEIGHT and WEIGHT tags
        #
        # For each HEIGHT, WEIGHT or AGE tags found in a RACE file,
        # we must call record_bioset_tags to record the AGE, HEIGHT and
        # WEIGHT tags.

        if (   $conversion_enable{'BIOSET:generate the new files'}
            && $filetype       eq 'RACE'
            && $line_info->[0] eq 'RACE'
            && (   exists $line_ref->{'AGE'}
                || exists $line_ref->{'HEIGHT'}
                || exists $line_ref->{'WEIGHT'} )
        ) {
            my ( $dir, $race, $age, $height, $weight );

            $dir  = File::Basename::dirname($file_for_error);
            $race = $line_ref->{ $master_order{'RACE'}[0] }[0];
            if ( $line_ref->{'AGE'} ) {
                $age = $line_ref->{'AGE'}[0];
                ewarn( WARNING, qq{Removing "$line_ref->{'AGE'}[0]"}, $file_for_error, $line_for_error );
                delete $line_ref->{'AGE'};
            }
            if ( $line_ref->{'HEIGHT'} ) {
                $height = $line_ref->{'HEIGHT'}[0];
                ewarn( WARNING, qq{Removing "$line_ref->{'HEIGHT'}[0]"}, $file_for_error, $line_for_error );
                delete $line_ref->{'HEIGHT'};
            }
            if ( $line_ref->{'WEIGHT'} ) {
                $weight = $line_ref->{'WEIGHT'}[0];
                ewarn( WARNING, qq{Removing "$line_ref->{'WEIGHT'}[0]"}, $file_for_error, $line_for_error );
                delete $line_ref->{'WEIGHT'};
            }

            record_bioset_tags( $dir, $race, $age, $height, $weight, $file_for_error,
                $line_for_error );
        }

        ##################################################################
        # [ 653596 ] Add a TYPE tag for all SPELLs
        # .

        if (   $conversion_enable{'SPELL:Add TYPE tags'}
            && exists $line_ref->{'SPELLTYPE'}
            && $filetype       eq 'CLASS'
            && $line_info->[0] eq 'CLASS'
        ) {

            # We must keep a list of all the SPELLTYPE for each class.
            # It is assume that SPELLTYPE cannot be found more than once
            # for the same class. It is also assume that SPELLTYPE has only
            # one value. SPELLTYPE:Any is ignored.

            my $class_name = $line_ref->{ $master_order{'CLASS'}[0] }[0];
            SPELLTYPE_TAG:
            for my $spelltype_tag ( values %{ $line_ref->{'SPELLTYPE'} } ) {
                my $spelltype = "";
                ($spelltype) = ($spelltype_tag =~ /SPELLTYPE:(.*)/);
                next SPELLTYPE_TAG if $spelltype eq "" or uc($spelltype) eq "ANY";
                $class_spelltypes{$class_name}{$spelltype}++;
            }
        }

        if (   $conversion_enable{'SPELL:Add TYPE tags'}
            && $filetype              eq 'SPELL'
            && $line_info->{Linetype} eq 'SPELL' )
        {

            # For each SPELL we build the TYPE tag or we add to the
            # existing one.
            # The .MOD SPELL are ignored.

        }

        # SOURCE line replacement
        # =======================
        # Replace the SOURCELONG:xxx|SOURCESHORT:xxx|SOURCEWEB:xxx
        # with the values found in the .PCC of the same directory.
        #
        # Only the first SOURCE line found is replaced.

        if (   $conversion_enable{'SOURCE line replacement'}
            && defined $line_info
            && $line_info->[0] eq 'SOURCE'
            && $source_curent_file ne $file_for_error )
        {

            # Only the first SOURCE tag is replace.
            if ( exists $source_tags{ File::Basename::dirname($file_for_error) } ) {

                # We replace the line with a concatanation of SOURCE tags found in
                # the directory .PCC
                my %line_tokens;
                while ( my ( $tag, $value )
                    = each %{ $source_tags{ File::Basename::dirname($file_for_error) } } )
                {
                    $line_tokens{$tag} = [$value];
                    $source_curent_file = $file_for_error;
                }

                $line_info->[1] = \%line_tokens;
            }
            elsif ( $file_for_error =~ / \A $cl_options{input_path} /xmsi ) {
                # We give this notice only if the curent file is under $cl_options{input_path}.
                # If -basepath is used, there could be files loaded outside of the -inputpath
                # without their PCC.
                ewarn( NOTICE, "No PCC source information found", $file_for_error, $line_for_error );
            }
        }

        # Extract lists
        # ====================
        # Export each file name and log them with the filename and the
        # line number

        if ( $conversion_enable{'Export lists'} ) {
            my $filename = $file_for_error;
            $filename =~ tr{/}{\\};

            if ( $filetype eq 'SPELL' ) {

                # Get the spell name
                my $spellname  = $line_ref->{'000SpellName'}[0];
                my $sourcepage = "";
                $sourcepage = $line_ref->{'SOURCEPAGE'}[0] if exists $line_ref->{'SOURCEPAGE'};

                # Write to file
                print { $filehandle_for{SPELL} }
                    qq{"$spellname","$sourcepage","$line_for_error","$filename"\n};
            }
            if ( $filetype eq 'CLASS' ) {
                my $class = ( $line_ref->{'000ClassName'}[0] =~ /^CLASS:(.*)/ )[0];
                print { $filehandle_for{CLASS} } qq{"$class","$line_for_error","$filename"\n}
                    if $class_name ne $class;
                $class_name = $class;
            }

            if ( $filetype eq 'DEITY' ) {
                print { $filehandle_for{DEITY} }
                    qq{"$line_ref->{'000DeityName'}[0]","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'DOMAIN' ) {
                print { $filehandle_for{DOMAIN} }
                    qq{"$line_ref->{'000DomainName'}[0]","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'EQUIPMENT' ) {
                my $equipname  = $line_ref->{ $master_order{$filetype}[0] }[0];
                my $outputname = "";
                $outputname = substr( $line_ref->{'OUTPUTNAME'}[0], 11 )
                    if exists $line_ref->{'OUTPUTNAME'};
                my $replacementname = $equipname;
                if ( $outputname && $equipname =~ /\((.*)\)/ ) {
                    $replacementname = $1;
                }
                $outputname =~ s/\[NAME\]/$replacementname/;
                print { $filehandle_for{EQUIPMENT} }
                    qq{"$equipname","$outputname","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'EQUIPMOD' ) {
                my $equipmodname = $line_ref->{ $master_order{$filetype}[0] }[0];
                my ( $key, $type ) = ( "", "" );
                $key  = substr( $line_ref->{'KEY'}[0],  4 ) if exists $line_ref->{'KEY'};
                $type = substr( $line_ref->{'TYPE'}[0], 5 ) if exists $line_ref->{'TYPE'};
                print { $filehandle_for{EQUIPMOD} }
                    qq{"$equipmodname","$key","$type","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'FEAT' ) {
                my $featname = $line_ref->{ $master_order{$filetype}[0] }[0];
                print { $filehandle_for{FEAT} } qq{"$featname","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'KIT STARTPACK' ) {
                my ($kitname)
                    = ( $line_ref->{ $master_order{$filetype}[0] }[0] =~ /\A STARTPACK: (.*) \z/xms );
                print { $filehandle_for{KIT} } qq{"$kitname","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'LANGUAGE' ) {
                my $languagename = $line_ref->{ $master_order{$filetype}[0] }[0];
                print { $filehandle_for{LANGUAGE} } qq{"$languagename","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'RACE' ) {
                my $racename        = $line_ref->{ $master_order{$filetype}[0] }[0];

                my $race_type = q{};
                $race_type = $line_ref->{'RACETYPE'}[0] if exists $line_ref->{'RACETYPE'};
                $race_type =~ s{ \A RACETYPE: }{}xms;

                my $race_sub_type = q{};
                $race_sub_type = $line_ref->{'RACESUBTYPE'}[0] if exists $line_ref->{'RACESUBTYPE'};
                $race_sub_type =~ s{ \A RACESUBTYPE: }{}xms;

                print { $filehandle_for{RACE} }
                    qq{"$racename","$race_type","$race_sub_type","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'SKILL' ) {
                my $skillname = $line_ref->{ $master_order{$filetype}[0] }[0];
                print { $filehandle_for{SKILL} } qq{"$skillname","$line_for_error","$filename"\n};
            }

            if ( $filetype eq 'TEMPLATE' ) {
                my $template_name = $line_ref->{ $master_order{$filetype}[0] }[0];
                print { $filehandle_for{TEMPLATE} } qq{"$template_name","$line_for_error","$filename"\n};
            }
        }

        ############################################################
        ######################## Conversion ########################
        # We manipulate the tags for the line here

        if ( $conversion_enable{'Generate BONUS and PRExxx report'} ) {
            for my $tag_type ( sort keys %$line_ref ) {
                if ( $tag_type =~ /^BONUS|^!?PRE/ ) {
                    $bonus_prexxx_tag_report{$filetype}{$_} = 1 for ( @{ $line_ref->{$tag_type} } );
                }
            }
        }

        1;
    }

}    # End of BEGIN

###############################################################
# additionnal_file_parsing
# ------------------------
#
# This function does additional parsing on each file once
# they have been seperated in lines of tags.
#
# Most commun use is for addition, convertion or removal of tags.
#
# Paramter: $lines_ref  Ref to an array containing lines of tags
#           $filetype   Type for the courent file
#           $filename   Name of the courent file
#
#           The $line_ref entries may now be in a new format, we need to find out
#           before using it. ref($line_ref) eq 'ARRAY'means new format.
#
#           The format is: [ $curent_linetype,
#                            \%line_tokens,
#                            $last_main_line,
#                            $curent_entity,
#                            $line_info,
#                          ];
#

{

    my %class_skill;
    my %class_spell;
    my %domain_spell;

    sub additionnal_file_parsing {
        my ( $lines_ref, $filetype, $filename ) = @_;

        ##################################################################
        # [ 779341 ] Spell Name.MOD to CLASS's SPELLLEVEL
        #

#  if($conversion_enable{'CLASS: SPELLLIST from Spell.MOD'})
#  {
#    if($filetype eq 'SPELL')
#    {
#      # All the Spell Name.MOD entries must be parsed to find the
#      # CLASSES and DOMAINS tags.
#      #
#      # The .MOD lines that have no other tags then CLASSES or DOMAINS
#      # will be removed entirely.
#
#      my ($directory,$spellfile) = File::Basename::dirname($filename);
#
#      for(my $i = 0; $i < @$lines_ref; $i++)
#      {
#        # Is this a .MOD line?
#        next unless ref($lines_ref->[$i]) eq 'ARRAY' &&
#                    $lines_ref->[$i][0] eq 'SPELL';
#
#        my $is_mod = $lines_ref->[$i][3] =~ /(.*)\.MOD$/;
#        my $spellname = $is_mod ? $1 : $lines_ref->[$i][3];
#
#        # Is there a CLASSES tag?
#        if(exists $lines_ref->[$i][1]{'CLASSES'})
#        {
#          my $tag = substr($lines_ref->[$i][1]{'CLASSES'}[0],8);
#
#          # We find each group of classes of the same level
#          for (split /\|/, $tag)
#          {
#            if(/(.*)=(\d+)$/)
#            {
#              my $level = $2;
#              my $classes = $1;
#
#              for my $class (split /,/, $classes)
#              {
#                #push @{$class_spell{
#              }
#            }
#            else
#            {
#              ewarn( NOTICE,  qq(!! No level were given for "$_" found in "$lines_ref->[$i][1]{'CLASSES'}[0]"),
#                $filename,$i );
#            }
#          }
#
##          ewarn( NOTICE,  qq(**** $spellname: $_),$filename,$i for @classes_by_level );
        #        }
        #
        #        if(exists $lines_ref->[$i][1]{'DOMAINS'})
        #        {
        #          my $tag = substr($lines_ref->[$i][1]{'DOMAINS'}[0],8);
        #          my @domains_by_level = split /\|/, $tag;
        #
        #          ewarn( NOTICE,  qq(**** $spellname: $_),$filename,$i for @domains_by_level );
        #        }
        #      }
        #    }
        #  }

        ###############################################################
        # Reformat multiple lines to one line for RACE and TEMPLATE.
        #
        # This is only useful for those who likes to start new entries
        # with multiple lines (for clarity) and then want them formatted
        # properly for submission.

        if ( $conversion_enable{'ALL:Multiple lines to one'} ) {
            my %valid_line_type = (
                'RACE'     => 1,
                'TEMPLATE' => 1,
            );

            if ( exists $valid_line_type{$filetype} ) {
                my $last_main_line = -1;

                # Find all the lines with the same identifier
                ENTITY:
                for ( my $i = 0; $i < @{$lines_ref}; $i++ ) {

                    # Is this a linetype we are interested in?
                    if ( ref $lines_ref->[$i] eq 'ARRAY'
                        && exists $valid_line_type{ $lines_ref->[$i][0] } )
                    {
                        my $first_line = $i;
                        my $last_line  = $i;
                        my $old_length;
                        my $curent_linetype = $lines_ref->[$i][0];
                        my %new_line        = %{ $lines_ref->[$i][1] };
                        $last_main_line = $i;
                        my $entity_name  = $lines_ref->[$i][3];
                        my $line_info    = $lines_ref->[$i][4];
                        my $j            = $i + 1;
                        my $extra_entity = 0;
                        my @new_lines;

                        #Find all the line with the same entity name
                        ENTITY_LINE:
                        for ( ; $j < @{$lines_ref}; $j++ ) {

                            # Skip empty and comment lines
                            next ENTITY_LINE
                                if ref( $lines_ref->[$j] ) ne 'ARRAY'
                                || $lines_ref->[$j][0] eq 'HEADER'
                                || ref( $lines_ref->[$j][1] ) ne 'HASH';

                            # Is it an entity of the same name?
                            if (   $lines_ref->[$j][0] eq $curent_linetype
                                && $entity_name eq $lines_ref->[$j][3] )
                            {
                                $last_line = $j;
                                $extra_entity++;
                                for ( keys %{ $lines_ref->[$j][1] } ) {

                                    # We add the tags except for the first one (the entity tag)
                                    # that is already there.
                                    push @{ $new_line{$_} }, @{ $lines_ref->[$j][1]{$_} }
                                        if $_ ne $master_order{$curent_linetype}[0];
                                }
                            }
                            else {
                                last ENTITY_LINE;
                            }
                        }

                        # If there was only one line for the entity, we do nothing
                        next ENTITY if !$extra_entity;

                        # Number of lines included in the CLASS
                        $old_length = $last_line - $first_line + 1;

                        # We prepare the replacement lines
                        $j = 0;

                        # The main line
                        if ( keys %new_line > 1 ) {
                            push @new_lines,
                                [
                                $curent_linetype,
                                \%new_line,
                                $last_main_line,
                                $entity_name,
                                $line_info,
                                ];
                            $j++;
                        }

                        # We splice the new class lines in place
                        splice @$lines_ref, $first_line, $old_length, @new_lines;

                        # Continue with the rest
                        $i = $first_line + $j - 1;    # -1 because the $i++ happen right after
                    }
                    elsif (ref $lines_ref->[$i] eq 'ARRAY'
                        && $lines_ref->[$i][0] ne 'HEADER'
                        && defined $lines_ref->[$i][4]
                        && $lines_ref->[$i][4]{Mode} == SUB )
                    {

                        # We must replace the last_main_line with the correct value
                        $lines_ref->[$i][2] = $last_main_line;
                    }
                    elsif (ref $lines_ref->[$i] eq 'ARRAY'
                        && $lines_ref->[$i][0] ne 'HEADER'
                        && defined $lines_ref->[$i][4]
                        && $lines_ref->[$i][4]{Mode} == MAIN )
                    {

                        # We update the last_main_line value and
                        # put the correct value in the curent line
                        $lines_ref->[$i][2] = $last_main_line = $i;
                    }
                }
            }
        }

        ###############################################################
        # [ 641912 ] Convert CLASSSPELL to SPELL
        #
        #
        # "CLASSSPELL"      => [
        #   'CLASS',
        #   'SOURCEPAGE',
        #   '#HEADER#SOURCE',
        #   '#HEADER#SOURCELONG',
        #   '#HEADER#SOURCESHORT',
        #   '#HEADER#SOURCEWEB',
        # ],
        #
        # "CLASSSPELL Level"      => [
        #   '000ClassSpellLevel',
        #   '001ClassSpells'

        if ( $conversion_enable{'CLASSSPELL convertion to SPELL'} ) {
            if ( $filetype eq 'CLASSSPELL' ) {

                # Here we will put aside all the CLASSSPELL that
                # we find for later use.

                my $dir = File::Basename::dirname($filename);

                ewarn(WARNING,
                      qq(Already found a CLASSSPELL file in $dir),
                      $filename
                ) if exists $class_spell{$dir};

                my $curent_name;
                my $curent_type = 2;    # 0 = CLASS, 1 = DOMAIN, 2 = invalid
                my $line_number = 1;

                LINE:
                for my $line (@$lines_ref) {

                    # We skip all the lines that do not begin by CLASS or a number
                    next LINE
                        if ref($line) ne 'HASH'
                        || ( !exists $line->{'CLASS'} && !exists $line->{'000ClassSpellLevel'} );

                    if ( exists $line->{'CLASS'} ) {

                        # We keep the name
                        $curent_name = ( $line->{'CLASS'}[0] =~ /CLASS:(.*)/ )[0];

                        # Is it a CLASS or a DOMAIN ?
                        if ( exists $valid_entities{'CLASS'}{$curent_name} ) {
                            $curent_type = 0;
                        }
                        elsif ( exists $valid_entities{'DOMAIN'}{$curent_name} ) {
                            $curent_type = 1;
                        }
                        else {
                            $curent_type = 2;
                            ewarn(WARNING,
                                  qq(Don\'t know if "$curent_name" is a CLASS or a DOMAIN),
                                  $filename,
                                  $line_number
                            );
                        }
                    }
                    else {
                        next LINE if $curent_type == 2 || !exists $line->{'001ClassSpells'};

                        # We store the CLASS name and Level

                        for my $spellname ( split '\|', $line->{'001ClassSpells'}[0] ) {
                            push @{ $class_spell{$dir}{$spellname}[$curent_type]
                                    { $line->{'000ClassSpellLevel'}[0] } }, $curent_name;

                        }
                    }
                }
                continue { $line_number++; }
            }
            elsif ( $filetype eq 'SPELL' ) {
                my $dir = File::Basename::dirname($filename);

                if ( exists $class_spell{$dir} ) {

                    # There was a CLASSSPELL in the directory, we need to add
                    # the CLASSES and DOMAINS tag for it.

                    # First we find all the SPELL lines and add the CLASSES
                    # and DOMAINS tags if needed
                    my $line_number = 1;
                    LINE:
                    for my $line (@$lines_ref) {
                        next LINE if ref($line) ne 'ARRAY' || $line->[0] ne 'SPELL';
                        $_ = $line->[1];

                        next LINE if ref ne 'HASH' || !exists $_->{'000SpellName'};
                        my $spellname = $_->{'000SpellName'}[0];

                        if ( exists $class_spell{$dir}{$spellname} ) {
                            if ( defined $class_spell{$dir}{$spellname}[0] ) {

                                # We have classes
                                # Is there already a CLASSES tag?
                                if ( exists $_->{'CLASSES'} ) {
                                    ewarn(WARNING,
                                          qq(The is already a CLASSES tag for "$spellname"),
                                          $filename,
                                          $line_number
                                    );
                                }
                                else {
                                    my @new_levels;
                                    for my $level ( sort { $a <=> $b }
                                        keys %{ $class_spell{$dir}{$spellname}[0] } )
                                    {
                                        my $new_level = join ',',
                                            @{ $class_spell{$dir}{$spellname}[0]{$level} };
                                        push @new_levels, "$new_level=$level";
                                    }
                                    my $new_classes = 'CLASSES:' . join '|', @new_levels;
                                    $_->{'CLASSES'} = [$new_classes];

                                    ewarn( WARNING,
                                        qq{SPELL $spellname: adding "$new_classes"},
                                        $filename,
                                        $line_number
                                    );
                                }
                            }

                            if ( defined $class_spell{$dir}{$spellname}[1] ) {

                                # We have domains
                                # Is there already a CLASSES tag?
                                if ( exists $_->{'DOMAINS'} ) {
                                    ewarn( WARNING,
                                        qq(The is already a DOMAINS tag for "$spellname"),
                                        $filename,
                                        $line_number
                                    );
                                }
                                else {
                                    my @new_levels;
                                    for my $level ( sort { $a <=> $b }
                                        keys %{ $class_spell{$dir}{$spellname}[1] } )
                                    {
                                        my $new_level = join ',',
                                            @{ $class_spell{$dir}{$spellname}[1]{$level} };
                                        push @new_levels, "$new_level=$level";
                                    }
                                    my $new_domains = 'DOMAINS:' . join '|', @new_levels;
                                    $_->{'DOMAINS'} = [$new_domains];

                                    ewarn( WARNING,
                                        qq{SPELL $spellname: adding "$new_domains"},
                                        $filename,
                                        $line_number
                                    );
                                }
                            }

                            # We remove the curent spell from the list.
                            delete $class_spell{$dir}{$spellname};
                        }
                    }
                    continue { $line_number++; }

                    # Second, we add .MOD line for the SPELL that were not present.
                    if ( keys %{ $class_spell{$dir} } ) {

                        # Put a comment line and a new header line
                        push @$lines_ref, "",
                            "###Block:SPELL.MOD generated from the old CLASSSPELL files";

                        for my $spellname ( sort keys %{ $class_spell{$dir} } ) {
                            my %newline = ( '000SpellName' => ["$spellname.MOD"] );
                            $line_number++;

                            if ( defined $class_spell{$dir}{$spellname}[0] ) {

                                # New CLASSES
                                my @new_levels;
                                for my $level ( sort { $a <=> $b }
                                    keys %{ $class_spell{$dir}{$spellname}[0] } )
                                {
                                    my $new_level = join ',',
                                        @{ $class_spell{$dir}{$spellname}[0]{$level} };
                                    push @new_levels, "$new_level=$level";
                                }
                                my $new_classes = 'CLASSES:' . join '|', @new_levels;
                                $newline{'CLASSES'} = [$new_classes];

                                ewarn( WARNING,
                                    qq{SPELL $spellname.MOD: adding "$new_classes"},
                                    $filename,
                                    $line_number
                                );
                            }

                            if ( defined $class_spell{$dir}{$spellname}[1] ) {

                                # New DOMAINS
                                my @new_levels;
                                for my $level ( sort { $a <=> $b }
                                    keys %{ $class_spell{$dir}{$spellname}[1] } )
                                {
                                    my $new_level = join ',',
                                        @{ $class_spell{$dir}{$spellname}[1]{$level} };
                                    push @new_levels, "$new_level=$level";
                                }

                                my $new_domains = 'DOMAINS:' . join '|', @new_levels;
                                $newline{'DOMAINS'} = [$new_domains];

                                ewarn(WARNING,
                                    qq{SPELL $spellname.MOD: adding "$new_domains"},
                                    $filename,
                                    $line_number
                                );
                            }

                            push @$lines_ref, [
                                'SPELL',
                                \%newline,
                                1 + @$lines_ref,
                                $spellname,
                                $master_file_type{SPELL}[1],    # Watch for the 1
                            ];

                        }
                    }
                }
            }
        }

        ###############################################################
        # [ 626133 ] Convert CLASS lines into 4 lines
        #
        # The 3 lines are:
        #
        # General (all tags not put in the two other lines)
        # Prereq. (all the PRExxx tags)
        # Class skills (the STARTSKILLPTS, the CKSILL and the CCSKILL tags)
        #
        # 2003.07.11: a forth line was added for the SPELL related tags

        if (   $conversion_enable{'CLASS:Four lines'}
            && $filetype eq 'CLASS' )
        {
            my $last_main_line = -1;

            # Find all the CLASS lines
            for ( my $i = 0; $i < @{$lines_ref}; $i++ ) {

                # Is this a CLASS line?
                if ( ref $lines_ref->[$i] eq 'ARRAY' && $lines_ref->[$i][0] eq 'CLASS' ) {
                    my $first_line = $i;
                    my $last_line  = $i;
                    my $old_length;
                    my %new_class_line = %{ $lines_ref->[$i][1] };
                    my %new_pre_line;
                    my %new_skill_line;
                    my %new_spell_line;
                    my %skill_tags = (
                        'CSKILL:.CLEAR' => 1,
                        CCSKILL         => 1,
                        CSKILL          => 1,
                        MODTOSKILLS     => 1,    #
                        MONSKILL        => 1,    # [ 1097487 ] MONSKILL in class.lst
                        MONNONSKILLHD   => 1,
                        SKILLLIST       => 1,    # [ 1580059 ] SKILLLIST tag
                        STARTSKILLPTS   => 1,
                    );
                    my %spell_tags = (
                        BONUSSPELLSTAT              => 1,
                        'BONUS:CASTERLEVEL'         => 1,
                        'BONUS:DC',                 => 1,  #[ 1037456 ] Move BONUS:DC on class line to the spellcasting portion
                        'BONUS:SCHOOL'              => 1,
                        'BONUS:SPELL'               => 1,
                        'BONUS:SPELLCAST'           => 1,
                        'BONUS:SPELLCASTMULT'       => 1,
                        'BONUS:SPELLKNOWN'          => 1,
                        CASTAS                      => 1,
                        ITEMCREATE                  => 1,
                        KNOWNSPELLS                 => 1,
                        KNOWNSPELLSFROMSPECIALTY    => 1,
                        MEMORIZE                    => 1,
                        PROHIBITED                  => 1,
                        SPELLBOOK                   => 1,
                        SPELLLEVEL                  => 1,
                        SPELLLIST                   => 1,
                        SPELLSTAT                   => 1,
                        SPELLTYPE                   => 1,
                    );
                    $last_main_line = $i;
                    my $class     = $lines_ref->[$i][3];
                    my $line_info = $lines_ref->[$i][4];
                    my $j         = $i + 1;
                    my @new_class_lines;

                    #Find the next line that is not empty or of the same CLASS
                    CLASS_LINE:
                    for ( ; $j < @{$lines_ref}; $j++ ) {

                        # Skip empty and comment lines
                        next CLASS_LINE
                            if ref( $lines_ref->[$j] ) ne 'ARRAY'
                            || $lines_ref->[$j][0] eq 'HEADER'
                            || ref( $lines_ref->[$j][1] ) ne 'HASH';

                        # Is it a CLASS line of the same CLASS?
                        if ( $lines_ref->[$j][0] eq 'CLASS' && $class eq $lines_ref->[$j][3] ) {
                            $last_line = $j;
                            for ( keys %{ $lines_ref->[$j][1] } ) {
                                push @{ $new_class_line{$_} }, @{ $lines_ref->[$j][1]{$_} }
                                    if $_ ne $master_order{'CLASS'}[0];
                            }
                        }
                        else {
                            last CLASS_LINE;
                        }
                    }

                    # Number of lines included in the CLASS
                    $old_length = $last_line - $first_line + 1;

                    # We build the two other lines.
                    for ( keys %new_class_line ) {

                        # Is it a SKILL tag?
                        if ( exists $skill_tags{$_} ) {
                            $new_skill_line{$_} = delete $new_class_line{$_};
                        }

                        # Is it a PRExxx tag?
                        elsif (/^\!?PRE/) {
                            $new_pre_line{$_} = delete $new_class_line{$_};
                        }

                        # Is it a SPELL tag?
                        elsif ( exists $spell_tags{$_} ) {
                            $new_spell_line{$_} = delete $new_class_line{$_};
                        }
                    }

                    # We prepare the replacement lines
                    $j = 0;

                    # The main line
                    if ( keys %new_class_line > 1
                        || ( !keys %new_pre_line && !keys %new_skill_line && !keys %new_spell_line )
                        )
                    {
                        push @new_class_lines,
                            [
                            'CLASS',
                            \%new_class_line,
                            $last_main_line,
                            $class,
                            $line_info,
                            ];
                        $j++;
                    }

                    # The PRExxx line
                    if ( keys %new_pre_line ) {

                        # Need to tell what CLASS we are dealing with
                        $new_pre_line{ $master_order{'CLASS'}[0] }
                            = $new_class_line{ $master_order{'CLASS'}[0] };
                        push @new_class_lines,
                            [
                            'CLASS',
                            \%new_pre_line,
                            ++$last_main_line,
                            $class,
                            $line_info,
                            ];
                        $j++;
                    }

                    # The skills line
                    if ( keys %new_skill_line ) {

                        # Need to tell what CLASS we are dealing with
                        $new_skill_line{ $master_order{'CLASS'}[0] }
                            = $new_class_line{ $master_order{'CLASS'}[0] };
                        push @new_class_lines,
                            [
                            'CLASS',
                            \%new_skill_line,
                            ++$last_main_line,
                            $class,
                            $line_info,
                            ];
                        $j++;
                    }

                    # The spell line
                    if ( keys %new_spell_line ) {

                        # Need to tell what CLASS we are dealing with
                        $new_spell_line{ $master_order{'CLASS'}[0] }
                            = $new_class_line{ $master_order{'CLASS'}[0] };

                        ##################################################################
                        # [ 876536 ] All spell casting classes need CASTERLEVEL
                        #
                        # BONUS:CASTERLEVEL|<class name>|CL will be added to all classes
                        # that have a SPELLTYPE tag except if there is also an
                        # ITEMCREATE tag present.

                        if (   $conversion_enable{'CLASS:CASTERLEVEL for all casters'}
                            && exists $new_spell_line{'SPELLTYPE'}
                            && !exists $new_spell_line{'BONUS:CASTERLEVEL'} )
                        {
                            my $class = $new_spell_line{ $master_order{'CLASS'}[0] }[0];

                            if ( exists $new_spell_line{'ITEMCREATE'} ) {

                                # ITEMCREATE is present, we do not convert but we warn.
                                ewarn(WARNING,
                                      "Can't add BONUS:CASTERLEVEL for class \"$class\", "
                                        . "\"$new_spell_line{'ITEMCREATE'}[0]\" was found.",
                                      $filename
                                );
                            }
                            else {

                                # We add the missing BONUS:CASTERLEVEL
                                $class =~ s/^CLASS:(.*)/$1/;
                                $new_spell_line{'BONUS:CASTERLEVEL'}
                                    = ["BONUS:CASTERLEVEL|$class|CL"];
                                ewarn( WARNING,
                                    qq{Adding missing "BONUS:CASTERLEVEL|$class|CL"},
                                    $filename
                                );
                            }
                        }

                        push @new_class_lines,
                            [
                            'CLASS',
                            \%new_spell_line,
                            ++$last_main_line,
                            $class,
                            $line_info,
                            ];
                        $j++;
                    }

                    # We splice the new class lines in place
                    splice @{$lines_ref}, $first_line, $old_length, @new_class_lines;

                    # Continue with the rest
                    $i = $first_line + $j - 1;    # -1 because the $i++ happen right after
                }
                elsif (ref $lines_ref->[$i] eq 'ARRAY'
                    && $lines_ref->[$i][0] ne 'HEADER'
                    && defined $lines_ref->[$i][4]
                    && $lines_ref->[$i][4]{Mode} == SUB )
                {

                    # We must replace the last_main_line with the correct value
                    $lines_ref->[$i][2] = $last_main_line;
                }
                elsif (ref $lines_ref->[$i] eq 'ARRAY'
                    && $lines_ref->[$i][0] ne 'HEADER'
                    && defined $lines_ref->[$i][4]
                    && $lines_ref->[$i][4]{Mode} == MAIN )
                {

                    # We update the last_main_line value and
                    # put the correct value in the curent line
                    $lines_ref->[$i][2] = $last_main_line = $i;
                }
            }
        }

        ###############################################################
        # The CLASSSKILL files must be deprecated in favor of extra
        # CSKILL in the CLASS files.
        #
        # For every CLASSSKILL found, an extra line must be added after
        # the CLASS line with the class name and the list of
        # CSKILL in the first CLASS file on the same directory as the
        # CLASSSKILL.
        #
        # If no CLASS with the same name can be found in the same
        # directory, entries with class name.MOD must be generated
        # at the end of the first CLASS file in the same directory.

        if ( $conversion_enable{'CLASSSKILL convertion to CLASS'} ) {
            if ( $filetype eq 'CLASSSKILL' ) {

                # Here we will put aside all the CLASSSKILL that
                # we find for later use.

                my $dir = File::Basename::dirname($filename);
                LINE:
                for ( @{ $lines_ref } ) {

                    # Only the 000ClassName are of interest to us
                    next LINE
                        if ref ne 'HASH'
                        || !exists $_->{'000ClassName'}
                        || !exists $_->{'001SkillName'};

                    # We preserve the list of skills for the class
                    $class_skill{$dir}{ $_->{'000ClassName'} } = $_->{'001SkillName'};
                }
            }
            elsif ( $filetype eq 'CLASS' ) {
                my $dir      = File::Basename::dirname($filename);
                my $skipnext = 0;
                if ( exists $class_skill{$dir} ) {

                    # There was a CLASSSKILL file in this directory
                    # We need to incorporate it

                    # First, we find all of the existing CLASS and
                    # add an extra line to them
                    my $index = 0;
                    LINE:
                    for (@$lines_ref) {

                        # If the line is text only, skip
                        next LINE if ref ne 'ARRAY';

                        my $line_tokens = $_->[1];

                        # If it is not a CLASS line, we skip it
                        next LINE
                            if ref($line_tokens) ne 'HASH'
                            || !exists $line_tokens->{'000ClassName'};

                        my $class = ( $line_tokens->{'000ClassName'}[0] =~ /CLASS:(.*)/ )[0];

                        if ( exists $class_skill{$dir}{$class} ) {
                            my $line_no = $- > [2];

                            # We build a new CLASS, CSKILL line to add.
                            my $newskills = join '|',
                                sort split( '\|', $class_skill{$dir}{$class} );
                            $newskills =~ s/Craft[ %]\|/TYPE.Craft\|/;
                            $newskills =~ s/Knowledge[ %]\|/TYPE.Knowledge\|/;
                            $newskills =~ s/Profession[ %]\|/TYPE.Profession\|/;
                            splice @$lines_ref, $index + 1, 0,
                                [
                                'CLASS',
                                {   '000ClassName' => ["CLASS:$class"],
                                    'CSKILL'       => ["CSKILL:$newskills"]
                                },
                                $line_no, $class,
                                $master_file_type{CLASS}[1],
                                ];
                            delete $class_skill{$dir}{$class};

                            ewarn( WARNING, qq{Adding line "CLASS:$class\tCSKILL:$newskills"}, $filename );
                        }
                    }
                    continue { $index++ }

                    # If there are any CLASSSKILL remaining for the directory,
                    # we have to create .MOD entries

                    if ( exists $class_skill{$dir} ) {
                        for ( sort keys %{ $class_skill{$dir} } ) {
                            my $newskills = join '|', sort split( '\|', $class_skill{$dir}{$_} );
                            $newskills =~ s/Craft \|/TYPE.Craft\|/;
                            $newskills =~ s/Knowledge \|/TYPE.Knowledge\|/;
                            $newskills =~ s/Profession \|/TYPE.Profession\|/;
                            push @$lines_ref,
                                [
                                'CLASS',
                                {   '000ClassName' => ["CLASS:$_.MOD"],
                                    'CSKILL'       => ["CSKILL:$newskills"]
                                },
                                scalar(@$lines_ref),
                                "$_.MOD",
                                $master_file_type{CLASS}[1],
                                ];

                            delete $class_skill{$dir}{$_};

                            ewarn( WARNING, qq{Adding line "CLASS:$_.MOD\tCSKILL:$newskills"}, $filename );
                        }
                    }
                }
            }
        }

        1;
    }

}

###############################################################
# mylength
# --------
#
# Find the number of characters for a string or a list of strings
# that would be separated by tabs.

sub mylength {
    return 0 unless defined $_[0];

    my @list;

    if ( ref( $_[0] ) eq 'ARRAY' ) {
        @list = @{ $_[0] };
    }
    else {
        @list = @_;
    }

    my $Length     = 0;
    my $beforelast = scalar(@list) - 2;

    if ( $beforelast > -1 ) {

        # All the elements except the last must be rounded to the next tab
        for my $subtag ( @list[ 0 .. $beforelast ] ) {
            $Length += ( int( length($subtag) / $tablength ) + 1 ) * $tablength;
        }
    }

    # The last item is not rounded to the tab length
    $Length += length( $list[-1] );

}

###############################################################
# check_clear_tag_order
# ---------------------
#
# Verify that the .CLEAR tags are put correctly before the
# tags that they clear.
#
# Parameter:  $line_ref       : Hash reference to the line
#             $file_for_error
#             $line_for_error

sub check_clear_tag_order {
    my ( $line_ref, $file_for_error, $line_for_error ) = @_;

    TAG:
    for my $tag ( keys %$line_ref ) {

        # if the current value is not an array, there is only one
        # tag and no order to check.
        next unless ref( $line_ref->{$tag} );

        # if only one of a kind, skip the rest
        next TAG if scalar @{ $line_ref->{$tag} } <= 1;

        my %value_found;

        if ( $tag eq "SA" ) {

            # The SA tag is special because it is only checked
            # up to the first (
            for ( @{ $line_ref->{$tag} } ) {
                if (/:\.?CLEAR.?([^(]*)/) {

                    # clear tag either clear the whole thing,
                    # in which case it must be the very beginning,
                    # or it clear a particular value, in which case
                    # it must be before any such value.
                    if ( $1 ne "" ) {

                        # Let's check if the value was found before
                        ewarn( NOTICE,  qq{"$tag:$1" found before "$_"}, $file_for_error, $line_for_error )
                            if exists $value_found{$1};
                    }
                    else {

                        # Let's check if any value was found before
                        ewarn( NOTICE,  qq{"$tag" tag found before "$_"}, $file_for_error, $line_for_error )
                            if keys %value_found;
                    }
                }
                elsif ( / : ([^(]*) /xms ) {

                    # Let's store the value
                    $value_found{$1} = 1;
                }
                else {
                    ewarn( ERROR,
                        "Didn't anticipate this tag: $_",
                        $file_for_error,
                        $line_for_error
                    );
                }
            }
        }
        else {
            for ( @{ $line_ref->{$tag} } ) {
                if (/:\.?CLEAR.?(.*)/) {

                    # clear tag either clear the whole thing,
                    # in which case it must be the very beginning,
                    # or it clear a particular value, in which case
                    # it must be before any such value.
                    if ( $1 ne "" ) {

                        # Let's check if the value was found before
                        ewarn( NOTICE, qq{"$tag:$1" found before "$_"}, $file_for_error, $line_for_error )
                            if exists $value_found{$1};
                    }
                    else {

                        # Let's check if any value was found before
                        ewarn( NOTICE, qq{"$tag" tag found before "$_"}, $file_for_error, $line_for_error )
                            if keys %value_found;
                    }
                }
                elsif (/:(.*)/) {

                    # Let's store the value
                    $value_found{$1} = 1;
                }
                else {
                    ewarn(ERROR,
                          "Didn't anticipate this tag: $_",
                          $file_for_error,
                          $line_for_error
                    );
                }
            }
        }
    }
}

###############################################################
# find_full_path
# --------------
#
# Change the @ and relative paths found in the .lst for
# the real thing.
#
# Parameters: $file_name            File name
#             $current_base_dir     Current directory
#             $base_path            Origin for the @ replacement

sub find_full_path {
    my ( $file_name, $current_base_dir, $base_path ) = @_;

    # Change all the \ for / in the file name
    $file_name =~ tr{\\}{/};

    # Replace @ by the base dir or add the current base dir to the file name.
    if( $file_name !~ s{ ^[@] }{$base_path}xmsi )
    {
        $file_name = "$current_base_dir/$file_name";
    }

    # Remove the /xxx/../ for the directory
    if ($file_name =~ / [.][.] /xms ) {
        if( $file_name !~ s{ [/] [^/]+ [/] [.][.] [/] }{/}xmsg ) {
            die qq{Can't des with the .. directory in "$file_name"};
        }
    }

    return $file_name;
}

###############################################################
# get_header
# ----------
#
# Return the correct header for a particular tag in a
# particular file type.
#
# If no tag is define for the filetype, the default for the
# tag is used. If not default, the tag name is returned.
#
# Parameters: $tag_name, $line_type

sub get_header {
    my ( $tag_name, $line_type ) = @_;
    my $header = $tagheader{$line_type}{$tag_name}
        || $tagheader{default}{$tag_name}
        || $tag_name;

    if ( $cl_options{missing_header} && $tag_name eq $header ) {
        $missing_headers{$line_type}{$header}++;
    }

    $header;
}

###############################################################
# create_dir
# ----------
#
# Create any part of a subdirectory structure that is not
# already there.

sub create_dir {
    my ( $dir, $outputdir ) = @_;

    # Only if the directory doesn't already exist
    if ( !-d $dir ) {
        my $parentdir = File::Basename::dirname($dir);

        # If the $parentdir doesn't exist, we create it
        if ( $parentdir ne $outputdir && !-d $parentdir ) {
            create_dir( $parentdir, $outputdir );
        }

        # Create the curent level directory
        mkdir $dir, oct(755) or die "Cannot create directory $dir: $OS_ERROR";
    }
}

###############################################################
# report_tag_sort
# ---------------
#
# Sort used for the tag when reporting them.
#
# Basicaly, it's a normal ASCII sort except that the ! are removed
# when found (the PRExxx and !PRExxx are sort one after the orther).

sub report_tag_sort {
    my ( $left, $right ) = ( $a, $b );    # We need a copy in order to modify

    # Remove the !. $not_xxx contains 1 if there was a !, otherwise
    # it contains 0.
    my $not_left  = $left  =~ s{^!}{}xms;
    my $not_right = $right =~ s{^!}{}xms;

    $left cmp $right || $not_left <=> $not_right;

}

###############################################################
# embedded_coma_split
# -------------------
#
# split a list using the coma but part of the list may be
# between brackets and the coma must be ignored there.
#
# Parameter: $list        List that need to be splited
#            $separator   optionnal expression used for the
#                         split, ',' is the default.
#
# Return the splited list.

sub embedded_coma_split {

    # The list may contain other lists between brackets.
    # We will first change all the , in within brackets
    # before doing our split.
    my ( $list, $separator ) = ( @_, ',' );

    return () unless $list;

    my $newlist;
    my @result;

    BRACE_LIST:
    while ($list) {

        # We find the next text within ()
        @result = Text::Balanced::extract_bracketed( $list, '()', qr([^()]*) );

        # If we didn't find any (), it's over
        if ( !$result[0] ) {
            $newlist .= $list;
            last BRACE_LIST;
        }

        # The prefix is added to $newlist
        $newlist .= $result[2];

        # We replace every , with &coma;
        $result[0] =~ s/,/&coma;/xmsg;

        # We add the bracket section
        $newlist .= $result[0];

        # We start again with what's left
        $list = $result[1];
    }

    # Now we can split
    return map { s/&coma;/,/xmsg; $_ } split $separator, $newlist;
}

###############################################################
# parse_system_files
# ------------------
#
# Parameter: $system_file_path  Path where the game mode folders
#                               can be found.

{
    # Needed for the Find function
    my @system_files;

    sub parse_system_files {
        my ($system_file_path) = @_;
        my $original_system_file_path = $system_file_path;

        my @verified_allowed_modes  = ();
        my @verified_stats          = ();
        my @verified_alignments     = ();
        my @verified_var_names      = ();
        my @verified_check_names    = ();

        # Set the header for the error messages
        set_ewarn_header( "================================================================\n"
                        . "Messages generated while parsing the system files\n"
                        . "----------------------------------------------------------------\n"
        );

        # Get the Unix direcroty separator even in a Windows environment
        $system_file_path =~ tr{\\}{/};

        # Verify if the gameModes directory is present
        if ( !-d "$system_file_path/gameModes" ) {
            die qq{No gameModes directory found in "$original_system_file_path"};
        }

        # We will now find all of the miscinfo.lst and statsandchecks.lst files
        @system_files = ();

        File::Find::find( \&want_system_info, $system_file_path );

        # Did we find anything (hopefuly yes)
        if ( scalar @system_files == 0 ) {
            ewarn ( ERROR,
                qq{No miscinfo.lst or statsandchecks.lst file were found in the system directory},
                $cl_options{system_path}
            );
        }

        # We only keep the files that correspond to the selected
        # game mode
        if ($cl_options{gamemode}) {
            @system_files
                = grep { m{ \A $system_file_path
                           [/] gameModes
                           [/] (?: $cl_options{gamemode} ) [/]
                         }xmsi;
                  }
                  @system_files;
        }

        # Anything left?
        if ( scalar @system_files == 0 ) {
            ewarn ( ERROR,
                qq{No miscinfo.lst or statsandchecks.lst file were found in the gameModes/$cl_options{gamemode}/ directory},
                $cl_options{system_path}
            );
        }

        # Now we search for the interesting part in the miscinfo.lst files
        for my $system_file (@system_files) {
            open my $system_file_fh, '<', $system_file;

            LINE:
            while ( my $line = <$system_file_fh> ) {
                chomp $line;

                # Skip comment lines
                next LINE if $line =~ / \A [#] /xms;

                # ex. ALLOWEDMODES:35e|DnD
                if ( my ($modes) = ( $line =~ / ALLOWEDMODES: ( [^\t]* )/xms ) ) {
                    push @verified_allowed_modes, split /[|]/, $modes;
                    next LINE;
                }
                # ex. STATNAME:Strength     ABB:STR DEFINE:MAXLEVELSTAT=STR|STRSCORE-10
                elsif ( $line =~ / \A STATNAME: /xms ) {
                    LINE_TAG:
                    for my $line_tag (split /\t+/, $line) {
                        # STATNAME lines have more then one interesting tags
                        if ( my ($stat) = ( $line_tag =~ / \A ABB: ( .* ) /xms ) ) {
                            push @verified_stats, $stat;
                        }
                        elsif ( my ($define_expression) = ( $line_tag =~ / \A DEFINE: ( .* ) /xms ) ) {
                            if ( my ($var_name) = ( $define_expression =~ / \A ( [^=|]* ) /xms ) ) {
                                push @verified_var_names, $var_name;
                            }
                            else {
                                ewarn( ERROR,
                                    qq{Can't find the variable name in "$define_expression"},
                                    $system_file,
                                    $INPUT_LINE_NUMBER
                                );
                            }
                        }
                    }
                }
                # ex. ALIGNMENTNAME:Lawful Good ABB:LG
                elsif ( my ($alingment) = ( $line =~ / \A ALIGNMENTNAME: .* ABB: ( [^\t]* ) /xms ) ) {
                    push @verified_alignments, $alingment;
                }
                # ex. CHECKNAME:Fortitude   BONUS:CHECKS|Fortitude|CON
                elsif ( my ($check_name) = ( $line =~ / \A CHECKNAME: .* BONUS:CHECKS [|] ( [^\t|]* ) /xms ) ) {
                    # The check name used by PCGen is actually the one defined with the first BONUS:CHECKS.
                    # CHECKNAME:Sagesse     BONUS:CHECKS|Will|WIS would display Sagesse but use Will internaly.
                    push @verified_check_names, $check_name;
                }
            }

            close $system_file_fh;
        }

        # We keep only the first instance of every list items and replace
        # the default values with the result.
        # The order of elements must be preserved
        my %seen = ();
        @valid_system_alignments = grep { !$seen{$_}++ } @verified_alignments;

        %seen = ();
        @valid_system_game_modes = grep { !$seen{$_}++ } @verified_allowed_modes;

        %seen = ();
        @valid_system_stats = grep { !$seen{$_}++ } @verified_stats;

        %seen = ();
        @valid_system_var_names = grep { !$seen{$_}++ } @verified_var_names;

        %seen = ();
        @valid_system_check_names = grep { !$seen{$_}++ } @verified_check_names;

        # Now we bitch if we are not happy
        if ( scalar @verified_stats == 0 ) {
            ewarn( ERROR,
                q{Could not find any STATNAME: tag in the system files},
                $original_system_file_path
            );
        }

        if ( scalar @valid_system_game_modes == 0 ) {
            ewarn( ERROR,
                q{Could not find any ALLOWEDMODES: tag in the system files},
                $original_system_file_path
            );
        }

        if ( scalar @valid_system_check_names == 0 ) {
            ewarn( ERROR,
                q{Could not find any valid CHECKNAME: tag in the system files},
                $original_system_file_path
            );
        }

        # If the -exportlist option was used, we generate a system.csv file
        if ( $cl_options{exportlist} ) {

            open my $csv_file, '>', 'system.csv';

            print {$csv_file} qq{"System Directory","$original_system_file_path"\n};

            if ( $cl_options{gamemode} ) {
                print {$csv_file} qq{"Game Mode Selected","$cl_options{gamemode}"\n};
            }
            print {$csv_file} qq{\n};

            print {$csv_file} qq{"Alignments"\n};
            for my $alignment (@valid_system_alignments) {
                print {$csv_file} qq{"$alignment"\n};
            }
            print {$csv_file} qq{\n};

            print {$csv_file} qq{"Allowed Modes"\n};
            for my $mode (sort @valid_system_game_modes) {
                print {$csv_file} qq{"$mode"\n};
            }
            print {$csv_file} qq{\n};

            print {$csv_file} qq{"Stats Abbreviations"\n};
            for my $stat (@valid_system_stats) {
                print {$csv_file} qq{"$stat"\n};
            }
            print {$csv_file} qq{\n};

            print {$csv_file} qq{"Variable Names"\n};
            for my $var_name (sort @valid_system_var_names) {
                print {$csv_file} qq{"$var_name"\n};
            }
            print {$csv_file} qq{\n};

            close $csv_file;
        }

        return;
    }

    sub want_system_info {
        push @system_files, $File::Find::name
            if lc $_ eq 'miscinfo.lst' || lc $_ eq 'statsandchecks.lst';
    };
}


###############################################################
# warn_deprecate
# --------------
#
# Generate a warning message about a deprecated tag.
#
# Parameters: $bad_tag          Tag that has been deprecated
#             $files_for_error  File name when the error is found
#             $line_for_error   Line number where the error is found
#             $enclosing_tag    (Optionnal) tag into which the
#                               deprecated tag is included

sub warn_deprecate {
    my ($bad_tag, $file_for_error, $line_for_error, $enclosing_tag) = (@_, "");

    my $message = qq{Deprecated syntax: "$bad_tag"};

    if($enclosing_tag) {
        $message .= qq{ found in "$enclosing_tag"};
    }

    ewarn( INFO, $message, $file_for_error, $line_for_error );

}

###############################################################
###############################################################
###
### Closure for ewarn, set_ewarn_filename and
### set_ewarn_header functions.
###

BEGIN {

    my $_file_name_previous = "";
    my $_header             = "";
    my $_is_first_error     = YES;
    my $_is_first_line      = YES;

    my %_warning_level_prefix = (
        7   => "DBG",   # DEBUG
        6   => "  -",   # INFO
        5   => "   ",   # NOTICE
        4   => "*=>",   # WARNING
        3   => "***",   # ERROR
    );

    ###############################################################
    # ewarn
    # -----
    #
    # Enhanced warn. Generate a warning message with the filename an line number.
    # if available.
    #
    # Possible levels:
    #
    #    DEBUG            Info message + debug message for the programmer
    #    INFO             Everything including deprecations message
    #    NOTICE           No deprecations (default)
    #    WARNING          PCGEN will prabably not work properly
    #    ERR              PCGEN will not work properly or the
    #                     script is foobar
    #
    #    The messages level are define with use constant
    #    commands at the beginning of the script.
    #
    # Parameter:
    #
    #   $warning_level    Warning level
    #   $message          Message text
    #   $file_name        Name of the file where the error is found
    #   $line_number      Line number where the error is found (optionnal)

    sub ewarn {
        my ( $warning_level, $message, $file_name, $line_number ) = ( @_, undef );

        # Verify if warning level should be displayed
        return if ( $cl_options{warning_level} < $warning_level );

        # Print the header if needed
        if ($_is_first_error) {
            warn $_header;
            $_is_first_error = NO;
            $_is_first_line  = NO;
        }

        # Windows and UNIX do not use the same charater in
        # the directory path. If we are on a Windows machine
        # we need to replace the / by a \.
        $file_name =~ tr{/}{\\} if $^O eq "MSWin32";

        # We make sure there is a new-line at the end of the message
        $message .= "\n" unless $message =~ /\n$/;

        # We display the file only if it is not the same are the last
        # time ewarn was called
        warn "$file_name\n" if $file_name ne $_file_name_previous;

        # We display the line number if there is one (optional parameter)
        if ( defined $line_number ) {
            warn $_warning_level_prefix{$warning_level}
                . "(Line $line_number): $message";
        }
        else {
            warn $_warning_level_prefix{$warning_level}
                . "$message";
        }

        # Remember the file name for next calls
        $_file_name_previous = $file_name;
    }

    ###############################################################
    # set_ewarn_filename
    # -------------------
    #
    # Replace the curent $_file_name_previous with a specific value.

    sub set_ewarn_filename {
        $_file_name_previous = $_[0];
    }

    ###############################################################
    # set_ewarn_header
    # -----------------
    #
    # Set the header for the error message and reset the
    # $_is_first_error to make the header display on the first
    # ewarn call for this header.

    sub set_ewarn_header {
        $_is_first_error = YES;

        $_header         = $_[0];

        # There is a leas line feed unless we are at the very
        # start of the log
        $_header         = "\n" . $_header unless $_is_first_line;

        # We blank the file name to make sure it will be printed
        # with the first message after the header.
        set_ewarn_filename('');
    }

}

###
### End of the ewarn, set_ewarn_filename and
### set_ewarn_header closure.
###
###############################################################
###############################################################


###############################################################
###############################################################
###
### Start of closure for BIOSET generation functions
### [ 663491 ] RACE: Convert AGE, HEIGHT and WEIGHT tags
###

{

    # Moving this out of the BEGIN as a workaround for bug
    # [perl #30058] Perl 5.8.4 chokes on perl -e 'BEGIN { my %x=(); }'

    my %RecordedBiosetTags;

    BEGIN {

        my %DefaultBioset = (

            # Race          AGE                     HEIGHT                              WEIGHT
            'Human' =>    [ 'AGE:15:1:4:1:6:2:6',   'HEIGHT:M:58:2:10:0:F:53:2:10:0',   'WEIGHT:M:120:2:4:F:85:2:4'     ],
            'Dwarf' =>    [ 'AGE:40:3:6:5:6:7:6',   'HEIGHT:M:45:2:4:0:F:43:2:4:0',     'WEIGHT:M:130:2:6:F:100:2:6'    ],
            'Elf' =>      [ 'AGE:110:4:6:6:6:10:6', 'HEIGHT:M:53:2:6:0:F:53:2:6:0',     'WEIGHT:M:85:1:6:F:80:1:6'      ],
            'Gnome' =>    [ 'AGE:40:4:6:6:6:9:6',   'HEIGHT:M:36:2:4:0:F:34:2:4:0',     'WEIGHT:M:40:1:1:F:35:1:1'      ],
            'Half-Elf' => [ 'AGE:20:1:6:2:6:3:6',   'HEIGHT:M:55:2:8:0:F:53:2:8:0',     'WEIGHT:M:100:2:4:F:80:2:4'     ],
            'Half-Orc' => [ 'AGE:14:1:4:1:6:2:6',   'HEIGHT:M:58:2:10:0:F:52:2:10:0',   'WEIGHT:M:130:2:4:F:90:2:4'     ],
            'Halfling' => [ 'AGE:20:2:4:3:6:4:6',   'HEIGHT:M:32:2:4:0:F:30:2:4:0',     'WEIGHT:M:30:1:1:F:25:1:1'      ],
        );

        ###############################################################
        # record_bioset_tags
        # ------------------
        #
        # This function record the BIOSET information found in the
        # RACE files so that the BIOSET files can later be generated.
        #
        # If the value are equal to the default, they are not generated
        # since the default apply.
        #
        # Parameters: $dir              Directory where the RACE file was found
        #             $race             Name of the race
        #             $age              AGE tag
        #             $height           HEIGHT tag
        #             $weight           WEIGHT tag
        #             $file_for_error   To use with ewarn
        #             $line_for_error   To use with ewarn

        sub record_bioset_tags {
            my ($dir,
                $race,
                $age,
                $height,
                $weight,
                $file_for_error,
                $line_for_error
            ) = @_;

            # Check to see if default apply
            RACE:
            for my $master_race ( keys %DefaultBioset ) {
                if ( index( $race, $master_race ) == 0 ) {

                    # The race name is included in the default
                    # We now verify the values
                    $age    = "" if $DefaultBioset{$master_race}[0] eq $age;
                    $height = "" if $DefaultBioset{$master_race}[1] eq $height;
                    $weight = "" if $DefaultBioset{$master_race}[2] eq $weight;
                    last RACE;
                }
            }

            # Everything that is not blank must be kept
            if ($age) {
                if ( exists $RecordedBiosetTags{$dir}{$race}{AGE} ) {
                    ewarn( NOTICE,
                        qq{BIOSET generation: There is already a AGE tag recorded}
                            . qq{ for a race named "$race" in this directory.},
                        $file_for_error,
                        $line_for_error
                    );
                }
                else {
                    $RecordedBiosetTags{$dir}{$race}{AGE} = $age;
                }
            }

            if ($height) {
                if ( exists $RecordedBiosetTags{$dir}{$race}{HEIGHT} ) {
                    ewarn( NOTICE,
                        qq{BIOSET generation: There is already a HEIGHT tag recorded}
                            . qq{ for a race named "$race" in this directory.},
                        $file_for_error,
                        $line_for_error
                    );
                }
                else {
                    $RecordedBiosetTags{$dir}{$race}{HEIGHT} = $height;
                }
            }

            if ($weight) {
                if ( exists $RecordedBiosetTags{$dir}{$race}{WEIGHT} ) {
                    ewarn( NOTICE,
                        qq{BIOSET generation: There is already a WEIGHT tag recorded}
                            . qq{ for a race named "$race" in this directory.},
                        $file_for_error,
                        $line_for_error
                    );
                }
                else {
                    $RecordedBiosetTags{$dir}{$race}{WEIGHT} = $weight;
                }
            }
        }

        ###############################################################
        # generate_bioset_files
        # ---------------------
        #
        # Generate the new BIOSET files from the data included in the
        # %RecordedBiosetTags hash.
        #
        # The new files will all be named bioset.lst and will required
        # to be renames and included in the .PCC manualy.
        #
        # No parameter

        sub generate_bioset_files {
            for my $dir ( sort keys %RecordedBiosetTags ) {
                my $filename = $dir . '/biosettings.lst';
                $filename =~ s/$cl_options{input_path}/$cl_options{output_path}/i;

                open my $bioset_fh, '>', $filename;

                # Printing the name of the new file generated
                print STDERR $filename, "\n";

                # Header part.
                print {$bioset_fh} << "END_OF_HEADER";
# CVS \$Revision\$ \$Author\$ -- $today -- reformated by $SCRIPTNAME v$VERSION

AGESET:0|Adulthood
END_OF_HEADER

                # Let's find the longest race name
                my $racename_length = 0;
                for my $racename ( keys %{ $RecordedBiosetTags{$dir} } ) {
                    $racename_length = length($racename) if length($racename) > $racename_length;
                }

                # Add the length for RACENAME:
                $racename_length += 9;

                # Bring the length to the next tab
                if ( $racename_length % $tablength ) {

                    # We add the remaining spaces to get to the tab
                    $racename_length += $tablength - ( $racename_length % $tablength );
                }
                else {

                    # Already on a tab length, we add an extra tab
                    $racename_length += $tablength;
                }

                # We now format and print the lines for each race
                for my $racename ( sort keys %{ $RecordedBiosetTags{$dir} } ) {
                    my $height_weight_line = "";
                    my $age_line           = "";

                    if (   exists $RecordedBiosetTags{$dir}{$racename}{HEIGHT}
                        && exists $RecordedBiosetTags{$dir}{$racename}{WEIGHT} )
                    {
                        my $space_to_add = $racename_length - length($racename) - 9;
                        my $tab_to_add   = int( $space_to_add / $tablength )
                            + ( $space_to_add % $tablength ? 1 : 0 );
                        $height_weight_line = 'RACENAME:' . $racename . "\t" x $tab_to_add;

                        my ($m_ht_min, $m_ht_dice, $m_ht_sides, $m_ht_bonus,
                            $f_ht_min, $f_ht_dice, $f_ht_sides, $f_ht_bonus
                            )
                            = ( split ':', $RecordedBiosetTags{$dir}{$racename}{HEIGHT} )
                            [ 2, 3, 4, 5, 7, 8, 9, 10 ];

                        my ($m_wt_min, $m_wt_dice, $m_wt_sides,
                            $f_wt_min, $f_wt_dice, $f_wt_sides
                            )
                            = ( split ':', $RecordedBiosetTags{$dir}{$racename}{WEIGHT} )
                                [ 2, 3, 4, 6, 7, 8 ];

# 'HEIGHT:M:58:2:10:0:F:53:2:10:0'
# 'WEIGHT:M:120:2:4:F:85:2:4'
#
# SEX:Male[BASEHT:58|HTDIEROLL:2d10|BASEWT:120|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]Female[BASEHT:53|HTDIEROLL:2d10|BASEWT:85|WTDIEROLL:2d4|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]

                        # Male height caculation
                        $height_weight_line .= 'SEX:Male[BASEHT:'
                            . $m_ht_min
                            . '|HTDIEROLL:'
                            . $m_ht_dice . 'd'
                            . $m_ht_sides;
                        $height_weight_line .= '+' . $m_ht_bonus if $m_ht_bonus > 0;
                        $height_weight_line .= $m_ht_bonus       if $m_ht_bonus < 0;

                        # Male weight caculation
                        $height_weight_line .= '|BASEWT:'
                            . $m_wt_min
                            . '|WTDIEROLL:'
                            . $m_wt_dice . 'd'
                            . $m_wt_sides;
                        $height_weight_line .= '|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]';

                        # Female height caculation
                        $height_weight_line .= 'Female[BASEHT:'
                            . $f_ht_min
                            . '|HTDIEROLL:'
                            . $f_ht_dice . 'd'
                            . $f_ht_sides;
                        $height_weight_line .= '+' . $f_ht_bonus if $f_ht_bonus > 0;
                        $height_weight_line .= $f_ht_bonus       if $f_ht_bonus < 0;

                        # Female weight caculation
                        $height_weight_line .= '|BASEWT:'
                            . $f_wt_min
                            . '|WTDIEROLL:'
                            . $f_wt_dice . 'd'
                            . $f_wt_sides;
                        $height_weight_line .= '|TOTALWT:BASEWT+(HTDIEROLL*WTDIEROLL)]';
                    }

                    if ( exists $RecordedBiosetTags{$dir}{$racename}{AGE} ) {

                        # We only generate a comment from the AGE tag
                        $age_line = '### Old tag for race '
                            . $racename . '=> '
                            . $RecordedBiosetTags{$dir}{$racename}{AGE};
                    }

                    print {$bioset_fh} $height_weight_line, "\n" if $height_weight_line;
                    print {$bioset_fh} $age_line,           "\n" if $age_line;

                    #      print BIOSET "\n";
                }

                close $bioset_fh;
            }
        }

    }    # BEGIN

}    # The entra encapsulation is a workaround for the bug
     # [perl #30058] Perl 5.8.4 chokes on perl -e 'BEGIN { my %x=(); }'

###
### End of  closure for BIOSET generation funcitons
###
###############################################################
###############################################################

###############################################################
# generate_css
# ------------
#
# Generate a new .css file for the .html help file.

sub generate_css {
    my ($newfile) = shift;

    open my $css_fh, '>', $newfile;

    print {$css_fh} << 'END_CSS';
BODY {
    font: small verdana, arial, helvetica, sans-serif;
    color: black;
    background-color: white;
}

A:link      {color: #0000FF}
A:visited   {color: #666666}
A:active    {color: #FF0000}


H1 {
    font: bold large verdana, arial, helvetica, sans-serif;
    color: black;
}


H2 {
    font: bold large verdana, arial, helvetica, sans-serif;
    color: maroon;
}


H3 {
    font: bold medium verdana, arial, helvetica, sans-serif;
        color: blue;
}


H4 {
    font: bold small verdana, arial, helvetica, sans-serif;
        color: maroon;
}


H5 {
    font: bold small verdana, arial, helvetica, sans-serif;
        color: blue;
}


H6 {
    font: bold small verdana, arial, helvetica, sans-serif;
        color: black;
}


UL {
    font: small verdana, arial, helvetica, sans-serif;
        color: black;
}


OL {
    font: small verdana, arial, helvetica, sans-serif;
        color: black;
}


LI
{
    font: small verdana, arial, helvetica, sans-serif;
    color: black;
}

TH {
    font: small verdana, arial, helvetica, sans-serif;
    color: blue;
}


TD {
    font: small verdana, arial, helvetica, sans-serif;
    color: black;
}

TD.foot {
    font: medium sans-serif;
    color: #eeeeee;
    background-color="#cc0066"
}

DL {
    font: small verdana, arial, helvetica, sans-serif;
    color: black;
}


DD {
    font: small verdana, arial, helvetica, sans-serif;
    color: black;
}


DT {
    font: small verdana, arial, helvetica, sans-serif;
        color: black;
}


CODE {
    font: small Courier, monospace;
}


PRE {
    font: small Courier, monospace;
}


P.indent {
    font: small verdana, arial, helvetica, sans-serif;
    color: black;
    background-color: white;
    list-style-type : circle;
    list-style-position : inside;
    margin-left : 16.0pt;
}

PRE.programlisting
{
    list-style-type : disc;
    margin-left : 16.0pt;
    margin-top : -14.0pt;
}


INPUT {
    font: bold small verdana, arial, helvetica, sans-serif;
    color: black;
    background-color: white;
}


TEXTAREA {
    font: bold small verdana, arial, helvetica, sans-serif;
    color: black;
    background-color: white;
}

.BANNER {
    background-color: "#cccccc";
    font: bold medium verdana, arial, helvetica, sans-serif;

}
END_CSS

    close $css_fh;
}

__END__

=head1 NAME

prettylst.pl -- Reformat the PCGEN .lst files

Version: 1.35

=head1 DESCRIPTION

B<prettylst.pl> is a script that parse a PCGEN .lst files and generate
new ones with the proper ordering of the fields. The original order was
given by Mynex. Nowadays, it's Tir-Gwait that is the
head-honcho-master-lst-monkey (well, he decide the order anyway :-).

The script is also able to do some conversions of the .lst so that old
versions are compatibled with the latest release of PCGEN.

=head1 INSTALLATION

=head2 Get Perl

I'm using ActivePerl v5.8.6 (build 811) but any standard distribution with version 5.5 and
over should work. The script has been tested on Windows 98, Windows 2000, Windows XP and FreeBSD.

To my knowledge, I'm using only one module that is not included in the standard distribution: Text::Balanced
(this module is included in the 5.8 standard distribution and maybe with some others).

To get Perl use <L<http://www.activestate.com/Products/ActivePerl/>> or <L<http://www.cpan.org/ports/index.html>>
To get Text::Balanced use <L<http://search.cpan.org/author/DCONWAY/Text-Balanced-1.89/lib/Text/Balanced.pm>> or
use the following command if you use the ActivePerl distribution:

  ppm install text-balanced

=head2 Put the script somewhere

Once Perl is installed on your computer, you just have to find a home for the script. After that,
all you have to do is type B<perl prettylst.pl> with the proper parameters to make it
work.

=head1 SYNOPSIS

  # parse all the files in PATH, create the new ones in NEWPATH
  # and produce a report of the TAG in usage
  perl prettylst.pl -inputpath=<PATH> -outputpath=<NEWPATH> -report
  perl prettylst.pl -i=<PATH> -o=<NEWPATH> -r

  # parse all the files in PATH and write the error messages in ERROR_FILE
  # without creating any new files
  perl prettylst.pl -inputpath=<PATH> -outputerror=<ERROR_FILE>
  perl prettylst.pl -i=<PATH> -e=<ERROR_FILE>

  # parse all the files in PATH and write the error messages in ERROR_FILE
  # without creating any new files
  # A compilation of cross-checking (xcheck) errors will not be displayed and
  # only the messages of warning level notice or worst will be outputed.
  perl prettylst.pl -noxcheck -warninglevel=notice -inputpath=<PATH> -outputerror=<ERROR_FILE>
  perl prettylst.pl -nx -wl=notice -i=<PATH> -e=<ERROR_FILE>

  # parse all the files in PATH and created new ones in NEWPATH
  # by applaying the convertion pcgen5713. The output is redirected
  # to ERROR_FILE
  perl prettylst.pl -inputpath=<PATH> -outputpath=<NEWPATH> \
                    -outputerror=<ERROR_FILE> -convert=pcgen5713
  perl prettylst.pl -i=<PATH> -o=<NEWPATH> -e=<ERROR_FILE> -c=pcgen5713

  # display the usage guide lines
  perl prettylst.pl -help
  perl prettylst.pl -h
  perl prettylst.pl -?

  # display the complete documentation
  perl prettylst.pl -man

  # generate and attemp to display a html file for
  # the complete documentation
  perl prettylst.pl -htmlhelp

=head1 PARAMETERS

=over 8

=item B<-inputpath> or B<-o>

Path to an input directory that will be scanned for .pcc files. A list of
files to parse will be built from the .pcc files found. Only the known filetypes will
be parsed.

If B<-inputpath> is given without any B<-outputpath>, the script parse the files, produce the
warning messages but doesn't write any new files.

=item B<-basepath> or B<-b>

Path to the base directory use to replace the @ character in the .PCC files. If no B<-basepath> option is given,
the value of B<-inputpath> is used to replace the @ character.

=item B<-systempath> or B<-s>

Path to the B<pcgen/system> used for the .lst files in B<-inputpath>. This directory should contain the
game mode files. These files will be parse to get a list of valid alignment abbreviations, valid statistic
abbriviations, valid game modes and globaly defined variables.

If the B<-gamemode> parameter is used, only the system files found in the proper game mode directory will
be parsed.

=item B<-outputpath> or B<-o>

Only used when B<-inputpath> is defined. B<-outputpath> define where the new files will
be writen. The directory tree from the B<-inputpath> will be reproduce as well.

Note: the output directory must be created before calling the script.

=item B<-outputerror> or B<-e>

Redirect STDERR to a file. All the warning and errors found by this script are printed
to STDERR.

=item B<-gamemode> or B<-gm>

Apply a filter on the GAMEMODE values and only read and/or reformat the files that
meet the filter.

e.g. -gamemode=35e

=item B<-convert> or B<-c>

Activate some convertions on the files. The converted files are written in the directory specified
by B<-outputpath>. If no B<-outputpath> is provided, the convertions messages are displayed but
no actual convertions are done.

Only one convertion may be activate at a time.

Here are the list of the valid convertions so far:

=over 12

=item B<pcgen5713>

Use to apply the convertions that bring the .lst files from v5.7.4 of PCGEN
to vertion 5.7.13.

=over 16

=item * [ 1070084 ] Convert SPELL to SPELLS

The old SPELL tags have been deprecated and must be replaced by SPELLS. This conversion
does only part of the job since not all the information needed by the new SPELLS tags
is present in the old SPELL tags.

<L<http://sourceforge.net/tracker/index.php?func=detail&aid=1070084&group_id=36698&atid=450221>>

=item * [ 1070344 ] HITDICESIZE to HITDIE in templates.lst

The old HITDICESIZE tag has been deprecated and my be replaced by the new HITDIE. HITDICESIZE
was only present in the TEMPLATE files.

<L<http://sourceforge.net/tracker/?func=detail&atid=578825&aid=1070344&group_id=36698>>

=item * [ 731973 ] ALL: new PRECLASS syntax

All the PRECLASS tag -- including the ones found within BONUS tags -- are converted to the new
syntax -- B<PRECLASS:E<lt>number of classesE<gt>,E<lt>list of classesE<gt>=E<lt>levelE<gt>>.

Note: this conversion was done a long time ago (pcgen511) but I've reactivated it since
a lot of old PRECLASS format have reaappear in the data sets resently.

<L<http://sourceforge.net/tracker/index.php?func=detail&aid=731973&group_id=36698&atid=450221>>

=back

=item B<pcgen574>


Use to apply the convertions that bring the .lst files from v5.6.x or v5.7.x of PCGEN
to vertion 5.7.4.

=over 16

=item * [ 876536 ] All spell casting classes need CASTERLEVEL

Add BONUS:CASTERLEVEL tags to casting that do not already have it.

<L<http://sourceforge.net/tracker/index.php?func=detail&aid=876536&group_id=36698&atid=417816>>

=item * [ 1006285 ] Convertion MOVE:<number> to MOVE:Walk,<Number>

The old MOVE tags are changed to the proper syntax i.e. the syntax that
identify the type of move. In this case, we assume that if no move
type were given, the move type is Walk.

<L<http://sourceforge.net/tracker/?func=detail&atid=450221&aid=1006285&group_id=36698>>

=back

=item B<pcgen56>

Use to apply the convertions that bring the .lst files from v5.4.x of PCGEN
to vertion 5.6.

=over 16

=item * [ 892746 ] KEYS entries were changed in the main files

Attempt at automaticaly conerting the KEYS entries that were changed in the
main xSRD files. Not all the changes were covered though.

<L<http://sourceforge.net/tracker/index.php?func=detail&aid=892746&group_id=36698&atid=578825>>

=back

=item B<pcgen555>

Use to apply the convertions that bring the .lst files from v5.4.x of PCGEN
to vertion 5.5.5.

=over 16

=item * [ 865826 ] Remove the deprecated MOVE tag in EQUIPMENT files

The MOVE tags are removed from the equipments files since they are now useless there.

<L<http://sourceforge.net/tracker/?func=detail&atid=450221&aid=865826&group_id=36698>>

=back

=item B<pcgen541>

Use to apply the convertions that bring the .lst files from v5.4 of PCGEN
to vertion 5.4.1.

=over 16

=item * [ 845853 ] SIZE is no longer valid in the weaponprof files

SIZE is removed WEAPONPROF files and is not replaced.

<L<http://sourceforge.net/tracker/index.php?func=detail&aid=845853&group_id=36698&atid=578825>>

=back

=item B<pcgen54>

Use this switch to convert from PCGEN 5.2 files to PCGGEN 5.4.

B<WARNING>: Do B<not> use this switch with B<CMP> files! You will break them.

=over 16

=item * [ 707325 ] PCC: GAME is now GAMEMODE

Strait change from one tag to the other. Why? Beats me but it sure helps the convertion script
buisiness to prosper :-).

<L<http://sourceforge.net/tracker/?func=detail&atid=450221&aid=707325&group_id=36698>>

=item * [ 784363 ] Add TYPE=Base.REPLACE to most BONUS:COMBAT|BAB

This change is needed to allow user to completely replace the BAB formulas with something
of their choice. For example, users can now have a customized class with
B<BONUS:COMBAT|BAB|TL|TYPE=Base> that would replace all the other Base bonus to BAB
(because it is greater).

<L<http://sourceforge.net/tracker/?func=detail&atid=450221&aid=784363&group_id=36698>>

=item * [ 825005 ] convert GAMEMODE:DnD to GAMEMODE:3e

PCGEN is droping the d20 licence. Because of that, the DnD keyword can no longer be used
as a game mode. As of PCGEN 5.4, the change to the system files were done and all the
.PCC files that linked to B<GAMEMODE:DnD> must now link to B<GAMEMODE:3e>.

<L<http://sourceforge.net/tracker/?func=detail&atid=578825&aid=825005&group_id=36698>>

B<WARNING>: Do B<not> use this convertion with B<CMP> files! You will break them.

=item * [ 831569 ] RACE:CSKILL to MONCSKILL

The new MONCSKILL tag along with the MFEAT and MONSTERCLASS are used when the default monsters
opotion is enabled in the PCGEN pref. Otherwise, the FEAT and CSKILL tags are used.

<L<http://sourceforge.net/tracker/?func=detail&atid=578825&aid=831569&group_id=36698>>

=back

=item B<pcgen534>

The following convertions were done on the .lst files between version 5.1.1 and 5.3.4 of PCGEN. See
the links for more information about the convertions in question.

=over 16

=item * [ 707325 ] PCC: GAME is now GAMEMODE

All the B<GAME> tags in the B<.PCC> files are converted to B<GAMEMODE> tags.

<L<https://sourceforge.net/tracker/?func=detail&atid=450221&aid=707325&group_id=36698>>

=item * [ 784363 ] Add TYPE=Base.REPLACE to most BONUS:COMBAT|BAB

All the B<BONUS:COMABAT|BAB> related to classes now have a B<TYPE=Base.REPLACE> added to them. This is
an important convertion if you want to mix files with the files included with PCGEN. If this is not done,
the BAB calculation will be all out of wack and you won't really now why.

<L<https://sourceforge.net/tracker/?func=detail&atid=450221&aid=784363&group_id=36698>>

=back

=item B<pcgen511>

The following convertions were done on the .lst files between version 4.3.4 and 5.1.1 of PCGEN. See
the links for more information about the convertions in question.

=over 16

=item * [ 699834 ] Incorrect loading of multiple vision types

=item * [ 728038 ] BONUS:VISION must replace VISION:.ADD

The B<VISION> tag used to allow the B<,> as a separator. This is no longer the case. Only the B<|>
can now be used as a separator. This convertion will replace all the B<,> by B<|> in the B<VISION>
tags except for those using the B<VISION:.ADD> syntax. The B<VISION:.ADD> tags are replaced by
B<BONUS:VISION> tags.

<L<https://sourceforge.net/tracker/?func=detail&atid=417816&aid=699834&group_id=36698>>
<L<https://sourceforge.net/tracker/?func=detail&atid=450221&aid=728038&group_id=36698>>

=item * [ 731973 ] ALL: new PRECLASS syntax

All the PRECLASS tag -- including the ones found within BONUS tags -- are converted to the new
syntax -- B<PRECLASS:E<lt>number of classesE<gt>,E<lt>list of classesE<gt>=E<lt>levelE<gt>>.

<L<http://sourceforge.net/tracker/index.php?func=detail&aid=731973&group_id=36698&atid=450221>>

=back

=item B<pcgen438>

The following convertions were done on the .lst files between version 4.3.3 and 4.3.4 of PCGEN. See
the links for more information about the convertions in question.

=over 16

=item * [ 686169 ] remove ATTACKS: tag

The B<ATTACKS> tags in the EQUIPMENT line types are replaced by B<BONUS:COMBAT|ATTACKS|> tags.

<L<https://sourceforge.net/tracker/?func=detail&atid=450221&aid=686169&group_id=36698>>

=item * [ 695677 ] EQUIPMENT: SLOTS for gloves, bracers and boots

The equipment of type Glove, Bracer and Boot need a B<SLOTS:2> tag if the pair must
be equiped to give the bonus. The convertion look at the equipement name and add
the B<SLOTS:2> tag is the item is in the plural form. If the equipment name is in the
singular, a message is printed to show that fact but the SLOTS:2 tag is not added.

<L<https://sourceforge.net/tracker/?func=detail&atid=450221&aid=695677&group_id=36698>>

=item * PRESTAT now only accepts the format PRESTAT:1,<stat>=<n>

The B<PRESTAT> no longer accept the old syntax. Now, every B<PRESTAT> tag needs a leading
number and coma before the stats enumaration. e.g. B<PRESTAT:STR=13> becaumes B<PRESTAT:1,STR=13>.

No tracker found.

=back

=item B<pcgen433>

This convert the references to equipement names and path that were changed with the release 4.3.3 of
PCGEN. This only change the path values in the .PCC, the files stay in the directories they are found.

=back

=item B<-old_source_tag>

From PCGen version 5.9.6, there is a new format for the SOURCExxx tag that use the tab instead of the |. prettylst.pl
automaticaly convert the SOURCExxx tags to the new format. The B<-old_source_tag> option must be used if
you want to keep the old format in place.

=back

=item B<-report> or B<-r>

Produce a report of the valid tags found in all the .lst and .pcc files. The report for
the invalid tags is always printed.

=item B<-xcheck> or B<-x>

B<This option is now on by default>

Verify the existance of values refered by other tags and produce a report of the
missing/inconsistant values.

=item B<-nojep>

Disable the new parse_jep function for the formula. This makes the script use the
old style formula parser.

=item B<-noxcheck> or B<-nx>

Disable the cross-check validations.

=item B<-warninglevel> or B<-wl>

Select the level of warning that should be displayed. The more critical levels include
the less critical ones. ex. B<-wl=informational> will output messages of level
informational, notice, warning and error but will not output the debug level messages.

The possible levels are:

=over 12

=item B<error>, B<err> or B<3>

Critical errors that need to be checked otherwise the resulting .lst files will not
work properly with PCGen.

=item B<warning>, B<warn> or B<4>

Important messages that should be verified. All the conversion messages are
at this level.

=item B<notice> or B<5>

The normal messages including common syntax mistakes and unknown tags.

=item B<informational>, B<info> or B<6> (default)

Can be very noisy. Include messages that warn about style, best practice and deprecated tags.

=item B<debug> or B<7>

Messages used by the programmer to debug the script.

=back

=item B<-exportlist>

Generate files which list objects with a reference on the file and line where they are located.
This is very useful when correcting the problems found by the -x options.

The files generated are:

=over 12

=item * class.csv

=item * domain.csv

=item * equipment.csv

=item * equipmod.csv

=item * feat.csv

=item * language.csv

=item * pcc.csv

=item * skill.csv

=item * spell.csv

=item * variable.csv

Z<>

=back

=item B<-missingheader> or B<-mh>

List all the requested headers (with the get_header function) that are not
defined in the %tagheader hash. When a header is not defined, the tag name
is used as is in the generated header lines.

=item B<-help>, B<-h> or B<-?>

Print a brief help message and exits.

=item B<-man>

Prints the manual page and exits. You might want to pipe the output to your favorite pager
(e.g. more).

=item B<-htmlhelp>

Generate a .html file with the complete documentation (as it is)
for the script and tries to display it in a browser. The display portion only
works on the Windows platform.

=back

=head1 MANIFEST

The distribution of this script includes the following files:

=over 8

=item * prettylst.pl

The script itself.

=item * prettylst.pl.html

HMTL version of the perldoc for the script. You can generate this file
by typing C<perl prettylst.pl -htmlhelp>.

=item * prettylst.pl.css

Style sheet files for prettylst.pl.html

=item * prettylst-release-notes-135.html

The release notes for the curent version.

=item * prettylst.pl.sig

PGP signature for the script. You can get a copy of my
key here: <L<http://pgp.mit.edu:11371/pks/lookup?op=get&search=0x5187D5D2>>

=back

=head1 COPYRIGHT

Copyright 2002 to 2006 by E<Eacute>ric E<quot>Space MonkeyE<quot> Beaudoin -- <mailto:beaudoer@videotron.ca>

All rights reserved.  You can redistribute and/or modify
this program under the same terms as Perl itself.

See L<http://www.perl.com/perl/misc/Artistic.html>.

=head1 TO DO

=over 8

=item * Add better examples

=item * Add more cross-reference checks

=back

=head1 KNOWN BUGS

=over 8

=item * The script is still unwilling to do the coffee...

=back

=head1 VERSION HISTORY

=head2 v1.35 -- Not yet released

[ 1335912 ] New tag: TEMPLATE:.CLEAR

[ 1580059 ] SKILLLIST tag

[ 1173567 ] Convert old style PREALIGN to new style

[ 1105603 ] New VARs in gameMode files

[ 1117152 ] VFEAT and TEMPLATE use

[ 1119767 ] Invalid value "R" for tag "MODS"

[ 1123650 ] HITDIE tag in class lines

[ 1152687 ] SPELLLEVEL:CLASS in feats.lst

[ 1153255 ] FUMBLERANGE new tag

[ 1156423 ] BONUS:WIELDCATEGORY

[ 1173534 ] .CLEAR syntax issue

[ 1173794 ] BONUS:WEAPONPROF order in race file

Eliminated a lot of false positive with references to SUBCLASS

Psionic is now valid in ADD:SPELLCASTER

Clean up the valid game modes

[ 1326008 ] Add tag: HIDETYPE to the PCC tag list

[ 1326016 ] New tag: PRERULE

[ 1325996 ] Add tag: ADD:EQUIP(y,y)z

[ 1325943 ] ADD:SKILL(Speak Language)1" found in FEAT

[ 1238595 ] New tag: PRECSKILL

[ 1326349 ] Missing TYPE:.CLEAR tag in FEAT

[ 1223873 ] WEAPONAUTO is no longer valid

[ 1326374 ] Add JEP operators

[ 1224428 ] No RACE entry for "SWITCHRACE:xxx"

[ 1282532 ] ClassDefense and Reputation

[ 1292967 ] TITLE and WORSHIPPERS in deity.lst

[ 1327238 ] Add CHANGEPROF to TEMPLATE tag list

[ 1324532 ] Biosettings.lst

[ 1309116 ] LANGAUTO missing in CLASS Level

Removed all the sub prototypes [Perl Best Practices]

mywarn has been completely replaced with ewarn

[ 1324512 ] BONUSSPELLSTAT is not in the CLASS tag list

[ 1355958 ] New tag: SCHOOL:.CLEAR

[ 1353231 ] New tag: RACETYPE

[ 1353233 ] New tag: RACESUBTYPE

[ 1355994 ] KIT file refinements

[ 1356139 ] UDAM missing in FEAT tag list

[ 1356143 ] ADD:Language missing in TEMPLATE tag

[ 1356158 ] SPELL is invalid as value for SPELLSTAT in CLASS

[ 1356999 ] Use of uninitialized value in string eq

[ 1359467 ] .COPY=<name> not used for validation

[ 1361057 ] Missing variables for the Modern game mode

[ 1361066 ] Do not x-check outside the -inputpath

Added system files parsing to find the variables names, game moes, and
abbreviations for stats and alignments

[ 1362206 ] [CLASS Level]Missing TEMPDESC tag

[ 1362222 ] [RACE]Missing KIT tag

[ 1362223 ] [CLASS Level]Missing BONUS:SLOTS

prettylst.pl no longer tolerate old style formula parser

[ 1364343 ] Multiple PRESPELLCAST tags

PRERACE:<number>,<list of races> is officialy the way to go

PRERACE:<list of races> to PRERACE:1,<list of races> conversion

[ 1367569 ] SYSTEM: Validate BONUS:CHECK with statsandchecks.lst values

[ 1366753 ] [KIT] The tag FREE is missing in the KIT FEAT tag list

[ 1398237 ] ALL: Convert Willpower to Will

Filter out the Subversion system directories

The SOURCExxx tags are now separated by tabs instead of |

The -old_source_tag option has been added to use | instead of tab in the SOURCExxx lines

Implemented a "fix" for the /../ in directories

[ 1440104 ] Ignore specific hidden files and directories

[ 1444527 ] New SOURCE tag format

[ 1483739 ] [CMP] SOURCEx changes for 5.10 compatibility

[ 1418243 ] RANGE:.CLEAR is missing in SPELL tag list

[ 1461407 ] ITEM: spell tag order

=head2 v1.34 -- 2005.01.19

[ 1028284 ] Verified if , are present in object names

[ 1028919 ] Report with GAMEMODE

[ 1028285 ] Convert old style PRExxx tags to new style

[ 1039028 ] [PCC]New Xcrawl Game Mode

[ 1070084 ] Convert SPELL to SPELLS

[ 1037456 ] Move BONUS:DC on class line to the spellcasting portion

[ 1027589 ] TEMPDESC (tag from 5.5.1) in skills.lst

[ 1066352 ] BONUS:COMBAT|INITIATIVE on MASTERBONUSRACE line

[ 1066355 ] BONUS tags in spells.lst

[ 1066359 ] BONUS:UDAM in class.lst

[ 1048297 ] New Tag: MONNONSKILLHD

[ 1077285 ] ALTCRITRANGE tag

[ 1079504 ] PREWIELD in eqmod file

[ 1083339 ] RATEOFFIRE in equip.lst

[ 1080142 ] natural attacks with TYPE:Natural

[ 1093382 ] Warning for missing param. in SPELLS

Added x-ref check for FOLLOWER and MASTERBONUSRACE in COMPANIONMODE file type

Added x-ref check for RACE with the PRERACE and !PRERACE tags

[ 1093134 ] BONUS:FEAT|POOL|x

[ 1094126 ] Make -xcheck option on by default

[ 1097487 ] MONSKILL in class.lst

[ 1104117 ] BL is a valid variable, like CL

[ 1104126 ] SPELLCASTER.Psionic is valid spellcasting class type

General work on KIT support

Three new file types added to exportlist: DEITY, KIT and TEMPLATE

DEITY, STARTPACK KIT and TEMPLATE are now validated by the x-check code

[ 1355926 ] DESC on equipment files

=head2 v1.33 -- 2004.08.29

[ 876536 ] All spell casting classes need CASTERLEVEL

[ 1003585 ] PCC: The script should not remove INCLUDE and EXCLUDE

The script can no longer read CLASSSPELL and CLASSSKILL files.

The functions CLASS_parse, CLASSSPELL_parse and GENERIC_parse have been removed since
they were no longer used.

[ 1004050 ] Spycrat is a new valid GAMEMODE

[ 971744 ] 5.7+ TEMPLATE in feats.lst

[ 976475 ] Missing LANGBONUS tag in CLASS Level

[ 1004081 ] Missing global BONUS:CASTERLEVEL

Major code reengeering to allow a better PRExxx tag validation

[ 1004893 ] ADD:SPELLCASTER is valid in RACE

[ 1005363 ] Validate NATURALATTACKS tag

[ 1005651 ] ADD:Language in a feat file

[ 1005653 ] Multiple variable names in a BONUS:VAR tag

[ 1005655 ] BONUS:SLOTS in race files

[ 1005658 ] BONUS:MOVEMULT

[ 1006285 ] Convertion MOVE:<number> to MOVE:Walk,<Number>

[ 1005661 ] ADD:SPELLCASTER in feat .lst

[ 1006985 ] Spycraft gameMode DEFINEd VARiables

[ 1006371 ] SA tag in Skill .lst

[ 976474 ] DEITY tag is missing from CLASS Level

Added the -gamemode parameter

=head2 v1.32 -- 2004.07.06

[ 832164 ] Adding NoProfReq to AUTO:WEAPONPROF for most races

[ 832171 ] AUTO:* needs to be separate tags

Added the -c=skillbonusfix to add BONUS:SKILL|Climb|8|TYPE=Racial if it is not already
present and the race has a MOVE:Climb entry. Same thing with Swim.

[ 845853 ] SIZE is no longer valid in the weaponprof files

[ 833509 ] All the PRExxx tags missing must be added

[ 849366 ] VFEAT with inline PRExxx

Added the ability to export the LANGUAGE entities when using the -exportlist option

[ 865826 ] Remove the deprecated MOVE tag in EQUIPMENT files

[ 865948 ] Properly check files with same name but different directory

[ 849365 ] CLASSES:ALL

[ 849369 ] SPELLCASTER.Arcane=1

[ 879467 ] AUTO:EQUIP in equipment files

[ 882797 ] SUBCLASS -- NAMEISPI: tag

[ 882799 ] SUBCLASSLEVEL -- add SPELLLEVEL:CLASS tag

[ 892746 ] KEYS entries were changed in the main files

[ 892748 ] Track the EQMOD keys with -x flag

Track the variable names with the -x flag (phase 1)

Put BONUS:CASTERLEVEL on the spell CLASS line

Removed a bunch of old convertion code that is no longer used

[ 971746 ] "PREVARGTEQ" can be used more than once in feats.lst

[ 971778 ] BONUS:UDAM| tag

Implemetend a workaroud for a perl bug => [perl #30058] Perl 5.8.4 chokes on perl -e 'BEGIN { my %x=(); }'

[ 902439 ] PREVISION not in FEAT tag list

[ 975999 ] [tab][space][tab] breaks prettylst

[ 974710 ] AUTO:WEAPONPROF usable multiple times

[ 971782 ] FACE tag in races.lst

Removed a warning message for CHOOSE:SPELLLEVEL

Add the B<-nowarning> option to suppress the warning messages

[ 974693 ] PROHIBITED class tag

=head2 v1.31 -- 2003.10.29

[ 823221 ] SPELL multiple time on equipment

[ 823763 ] BONUS:DC in class level

[ 823764 ] ADD:FEAT in domain list

[ 824975 ] spells.lst - DESCISPI:[YES/NO]

[ 825005 ] convert GAMEMODE:DnD to GAMEMODE:3e

[ 829329 ] Lines get deleted when the line type is not know

[ 829335 ] New LANGAUTO line type for KIT files

[ 829380 ] New Game Mode

[ 831569 ] RACE:CSKILL to MONCSKILL

[ 832139 ] CLASS Level: missing NATURALATTACKS

=head2 v1.30 -- 2003.10.14

[ 804091 ] ADD:FEAT warning

[ 807329 ] PRESIZE warning (for template.lst)

[ 813333 ] MONCSKILL and MONCCSKILL in race.lst

[ 813334 ] PREMULT

[ 813335 ] ACHECK:DOUBLE

[ 813337 ] BONUS:DC

[ 813504 ] SPELLLEVEL:DOMAIN in domains.lst

[ 814200 ] PRESKILL in SPELL files

[ 817399 ] Tags usable in SUBCLASS

[ 823042 ] not finding files issue

A new B<-baspath> option was added to specify the path that must replace the @ characters in
the .PCC files when that path is different from B<-inputpath>.

[ 823166 ] Missing PREVARNEQ tag

[ 823194 ] PREBASESIZExxx tags

=head2 v1.29 -- 2003.08.23

New tags were added as a result of the big CMP push.

The script now detect the tags that have no values (with the -x option).

PRECLASS:Spellcaster, Spellcaster.Arcane and Spellcaster.Devine are now understood.

Removed the 4.3.3 dir restructure convertion code.

I've activated the KIT files reformating.

The CLASS lines are now reformated in four lines. A new line with all the spell related
tags follow the skill tags.

[ 707325 ] PCC: GAME is now GAMEMODE L<https://sourceforge.net/tracker/?func=detail&atid=450221&aid=707325&group_id=36698>

New set_ewarn_header function

New function to take RACE and TEMPLATE that are on multiple lines and bring them back to one line

[ 779821 ] Add quote removal L<https://sourceforge.net/tracker/?func=detail&atid=578825&aid=779821&group_id=36698>

[ 784363 ] Add TYPE=Base.REPLACE to most BONUS:COMBAT|BAB L<https://sourceforge.net/tracker/?func=detail&atid=450221&aid=784363&group_id=36698>

=head2 v1.28 -- 2003.05.04

New line type MASTERBONUSRACE

New validation for the FEAT line type (CHOSE <=> MULT <=> STACK)

[ 728038 ] BONUS:VISION must replace VISION:.ADD

[ 711565 ] BONUS:MOVE replaced with BONUS:MOVEADD (Not definitive yet)

New validation for PRECLASS (make sure the number is there and the class exists)

[ 731973 ] ALL: new PRECLASS syntax

=head2 v1.27 -- 2003.04.03

The B<-inputpath> option is now mandantory

[ 686169 ] remove ATTACKS: tag

[ 695677 ] EQUIPMENT: SLOTS for gloves, bracers and boots

[ 707325 ] PCC: GAME is now GAMEMODE

[ 699834 ] Incorrect loading of multiple vision types

PRESTAT now only accepts the format PRESTAT:1,<stat>=<n>

=head2 v1.26 -- 2002.02.27

[ 677962 ] The DMG wands have no charge

Removed the invalid PREBAB tag

Change the order for the FEAT line type

Dir path convertion for the new SRD files

Upgraded to ActivePerl 635

New EQUIPMENT tag order

Weapon name convertion for PCGEN 4.3.3 for SRD compliance

New B<-convert> parameter

=head2 v1.25 -- 2003.01.27

[ 670554 ] SYNERGY to BONUS:SKILL format

Fixed the CLASSSPELL conversion that was not working with the new parser

Fixed a problem with the Export Lists function (for DOMAIN)

Change the BIOSET convertion code so that the new bioset files are
generated in the output directory

New SKILL line tags order

=head2 v1.24 -- 2003.01.14

BIOSET generation from the AGE, HEIGHT and WEIGHT tags

Added the BIOSET file definition for FILETYPE_parse

New order for SPELL tags

=head2 v1.23 -- 2003.01.06

I'm removed the useles -debug option

Add a bunch of new tags in the SUBCLASSLEVEL (everything in CLASS Level)

I'm now running Perl Dev Kit 5

=head2 v1.22 -- 2002.12.31

The FEAT validation code now deal with |CHECKMULT properly

The FEAT validation code now ignores , between () for ADD:FEAT and PREFEAT

Fixed remaining tr!/!\\! so that they are used only on MSWin32 systems

The new set_mywan_filename is called after each section header to empty the $previousfile
variable within the mywarn closure

=head2 v1.21 -- 2002.12.28

FEAT validation added for the tags FEAT, MFEAT, VFEAT, PREFEAT and ADD:FEAT

[ 657059 ] Verify pipe is the only delimiter:VISION

The tr!/!\\! on the file names printed by mywarn is done only for MSWin32 OS

=head2 v1.20 -- 2002.12.19

[ 653596 ] Add a TYPE tag for all SPELLs (first part, change on hold)

Added the -missingheader command line option to list all the header that do not
have definitions in the %tagheader hash

Only the first SOURCExxx line is replaced when the SOURCE line replacement option
is active

All the filetypes except CLASSSKILL and CLASSSPELL have been
migrated to FILETYPE_parse (KIT is only validated for now)

New .CLEAR code (TAG:.CLEAR are all different tags now)

=head2 v1.19 -- 2002.12.12

[ 602874 ] SAVES tag deprecated, replaced by 3 BONUS:CHECKS|BASE.savename|x|PREDEFAULTMONSTER:Y

The CVS files beginning with .# are now ignored by prettylst

Code to correct the BONUS:STAT|WIL typo (should be BONUS:STAT|WIS)

New NAMEISPI tag in every files

New get_header function

The BONUS:xxx are now considered differents tags (like the ADD:xxx)

[ 609763 ] Convert the old PRECHECKxxx

Added coded to check and standerdize tags with limited possible values

SA:.CLEAR is now a sperate tag than SA: in order to facilitate the sorting

Got rid of the old %validpcctag (replaced by the generic %valid_tags)

[ 619312 ] RACENAME deprecated, convert to OUTPUTNAME

[ 613604 ] CASTAS:name to SPELLLIST:x|name

Added code to standardise the SOURCExxx line in the .lst files
based of the SOURCExxx tags found in the same directory.

Added code to convert the CLASSSKILL files into CLASS CSKILL

[ 620419 ] Added code to flag and display the SA entries that include ','

[ 624885 ] CLASS: remove AGESET tag

[ 626133 ] Convert CLASS lines into 3 lines

Changed the report sort order so that !PRExxx entries are now sorted
right after the corresponding PRExxx.

Added code for CSKILL, LANGAUTO and LANGBONUS tag validation

[ 641912 ] Convert CLASSSPELL to SPELL

New FILETYPE_parser

Removed the now useless -taginfixed option.

New ###Block pragma. It forces a new block for the entities that have
block formatting (FILETYPE_parse only)

Added the KIT filetype

Convertion code for EFFECTS to DESC and EFFECTTYPE to TARGETAREA in the SPELL files

=head2 v1.18 -- 2002.08.31

Conversion of the stat tags in TEMPLATE (STR, DEX, etc.) by BONUS:STAT|...

Removing TYPE=Ability from BONUS:STAT|xxx|y|TYPE=Ability in RACE

Added the COPYRIGHT tags for the PCC files

Conversion of nameCHECK to BONUS:CHECKS|BASE.name in CLASS

Conversion of BAB to BONUS:COMBAT|BAB in CLASS

Remove the GOLD tag from CLASS and TEMPLATE for OGL compliance

New tag MODTOSKILLS

Deprecated INTMODTOSKILLS

Fixed a bug with #EXTRAFILE that was introduced in parse_tag

=head2 v1.17 -- 2002.08.17

New file type COMPANIONMOD

New tag INFOTEXT

Added convertion code for the STATADJx tags

Add a few of the missing GLOBAL tags

Removed a few illigal BONUS type

[ 571276 ] "PRESKILL:1,Knowledge %" replace by "PRESKILL:1,TYPE.Knowledge" in the CLASS lines

[ xxx ] "SUBSA:blah" must become "SA:.CLEAR.blah". The new SA tags
must be put before the existing SA tags.


=head2 v1.16 -- 2002.06.28

Add code to correct the convertion mistake and also corrected the convertion matrice
for the new SKILL tags.

First phase of cross-check validation.

Corrected a bug with the line number.

Add convertion for PRETYPE:Magic to PRETYPE:EQMODTYPE=MagicalEnhancement in the
EQUIPMOD files.

Add -x option to do x-check validation.

Add validation for the .MOD entries.

Add convertion for SR to SPELLRES in SPELL files.

=head2 v1.15 -- 2002.06.20

New option B<-outputerror> to redirect STDERR in a file

Preserve the leading spaces on the first column when the pragma #prettylst:leadingspaces:ignore
is used. The pragma #prettylst:leadingspaces:trim restore normal space triming.

Replace the deprecated PREVAR for PREVARGT.

Add new DOMAIN tags

Add new DEITY tags

Add new RACE tags

PCGEN now check to see if existing comment line exists before adding a new one. Existing
header lines are genereted to reflect the curent TAGs in used.

Add new SKILL tags

Add new SPELL tags

Add new CLASS tags

=head2 v1.14 -- 2002.06.08

The files are now written if there is no other change then the CF corrections

Add the internal WriteLog function

Change the order for the RACE filetype as requested by Andrew McDougall (tir-gwaith)

RACE filetype: convert INIT:xx to BONUS:COMBAT|Initiative|xx and deprecate INIT

CLASS filetype: convert ADD:INIT|xx to BONUS:COMBAT|Initiative|xx and deprecate ADD:INIT

RACE filetype: added code to remove AC and replace it by BONUS:COMBAT|AC|xx|TYPE=NaturalArmor
when needed

EQUIPMENT filetype: added code to replace all the Cost by COST

Add code to deal with .MOD in all the files except CLASS and CLASSSPELL

=head2 v1.13 -- 2002.05.11

Now parse the BONUS tags.

Change the sort of the CLASS Level lines. Multiple tags on the same type are no
longer on the same column.

Skip empty files.

=head2 v1.12 -- 2002.03.23

Add code to replace the BONUS:FEAT, BONUS:VFEAT and FEAT in the EQUIPMENT by
VFEAT.

Remove the empty columns for the CLASS lines.

Added the parse_tag function for all the tags.

Deprecate the NATURALARMOR tag and added code to convert to
BONUS:COMBAT|AC|x|Type=Natural

=head2 v1.11 -- 2002.03.07

Add code to deal with the CR-CR-LF stuff in the .lst files

The comment generated by PCGEN now contains the CVS Revision and Author tags

The CLASS level lines have a new sort order.

Remove CCOST and RREPLACE from tags (these were typos)

Change findfullpath for the new behavior of the @ character in file paths.

Added code to check the GAME and TYPE tags in the .PCC files

Added code to verify the existance of every file for each .PCC

=head2 v1.10 -- 2002.02.27

Bug fixes

=head2 v1.09 -- 2002.02.20

Add a optional check to see if a TAG has been put in a fixed column. If such ':' is
found in one of the fixed column, a warning is printed.

Check for all file extention to find the unlinked files that are not .lst

Add support fot the E<quot>pragmaE<quot> tag #EXTRAFILE

Add code to convert SKILL to BONUS:SKILL in RACE files

The DEITY tag in the CLASS files was deprecated

=head2 v1.08 -- 2002.02.17

Only write the .pcc files that have an extra 0x0d character or white spaces at the
end of the line.

Add support for the new SOURCEPAGE, SOURCEWEB, SOURCELONG and SOURCESHORT tags.

Add conversion code that replace the SOURCE:p. tags by SOURCEPAGE:p. tags.

Add convertion code that remove the ROOT tags in the SKILL files and add the
new format of the TYPE tag.

Remove the ROOT tag from the SKILL filetype. This tag is now deprecate.

Romove any quote found.

=head2 v1.07 -- 2002.02.08

Bug with the WEAPONBONUS tag being there twice for the RACE filetype

Add code to detect if one of the tags is there more then once for
a particular filetype

The odd end of lines (CR-CR-LF) are striped when the files that get rewriten

Produce a list of files not found in the .PCC files

=head2 v1.06 -- 2002.02.07

Add support for CLASSSPELL

Add support for TEMPLATE

Add support for WEAPONPROF

The script now adds a dummy SOURCE:p. tag in some files when none are found.

=head2 v1.05 -- 2002.02.06

Add support for CLASSSKILL files (OK, this one was not very hard...)

Add support for DIETY files

Add support for DOMAIN files

Add support for FEAT files

Add support for LANGUAGE files

Add support for SKILL files

Add support for SPELL files

Unknown tags are kept (including duplicates)

=head2 v1.04 -- 2002.02.05

Add support for RACE files

Add support for EQUIMOD files

Most files are now parse by a Generic parser

Unknown tags are kept (including duplicates)

=head2 v1.03 -- 2002.02.03

Change the sort order for the additionnal lines

=head2 v1.02 -- 2002.02.03

No more empty white spaces between the columns in for the level advancement lines


=head2 v1.01 -- 2002.02.02

Add support for the CLASS files

Check and remove extra space at the end of each tab separated TAG

Add special case for the ADD:adlib tags

=head2 v1.00 -- 2002.01.27

First working version. Only the EQUIPMENT file are supported.

