#!/usr/bin/perl
# convert_514.pl Copyright (c) 2008 Chris 'Barak' Chandler
# This script reads PCGEN .lst files and reformats them from 5.12 syntax to 5.14 syntax

use strict;
use warnings;
use File::Find;

my $input_dir;
my $output_dir;
my $out_path;
my $base_name;
my @schools = (
                "Abjuration", "Conjuration", "Divination", "Enchantment",
                "Evocation",  "Illusion",    "Necromancy", "Transmutation",
                "Universal", "Clairsentience", "Metacreativity", "Psychokinesis",
                "Psychometabolism", "Psychoportation"
);
my @subschools = (
                   "Calling",  "Charm",
                   "Creation", "Compulsion",
                   "Figment",  "Glamer",
                   "Healing",  "Pattern",
                   "Phantasm", "Scrying",
                   "Shadow",   "Summoning",
                   "Teleportation"
);
my @descriptors = (
                    "Acid",               "Air",
                    "Chaotic",            "Cold",
                    "Darkness",           "Death",
                    "Electricity",        "Evil",
                    "Fear",               "Fire",
                    "Force",              "Good",
                    "Language-Dependent", "Lawful",
                    "Light",              "Mind-Affecting",
                    "Sonic",              "Water"
);
my @choose_subtokens = (
                    "ABILITY",                   "ARMORPROF",
                    "ARMORTYPE",                 "CCSKILLLIST",
                    "CSKILLS",                   "COUNT",
                    "DOMAIN",                    "EQBUILDER.SPELL",
                    "EQUIPTYPE",                 "FEAT",
                    "FEATADD",                   "FEATLIST",
                    "FEATSELECT",                "HP",
                    "LANGAUTO",                  "LANGUAGE",
                    "NOCHOICE",                  "NONCLASSSKILLLIST",
                    "NUMBER",                    "NUMCHOICES",
                    "PROFICIENCY",               "RACE",
                    "SALIST",                    "SCHOOLS",
                    "SHIELDPROF",                "SKILLLIST",
                    "SKILLS",                    "SKILLSNAMED",
                    "SKILLSNAMEDTOCCSKILL",      "SKILLSNAMEDTOCSKILL",
                    "SPELLCLASSES",              "SPELLLEVEL",
                    "SPELLLIST",                 "SPELLS",
                    "STAT",                      "STRING",
                    "TEMPLATE",                  "USERINPUT",
                    "WEAPONFOCUS",               "WEAPONPROF",
                    "WEAPONPROFS"
);
my %subclass = (
                 Wizard       => "Wizard.Wizard",
                 Abjurer      => "Wizard.Abjurer",
                 Conjurer     => "Wizard.Conjurer",
                 Diviner      => "Wizard.Diviner",
                 Enchanter    => "Wizard.Enchanter",
                 Evoker       => "Wizard.Evoker",
                 Illusionist  => "Wizard.Illusionist",
                 Necromancer  => "Wizard.Necromancer",
                 Transmuter   => "Wizard.Transmuter",
                 Seer         => "Psion.Seer",
                 Shaper       => "Psion.Shaper",
                 Kineticist   => "Psion.Kineticist",
                 Egoist       => "Psion.Egoist",
                 Nomad        => "Psion.Nomad",
                 Telepath     => "Psion.Telepath"
);
my %alignments = (
                    0 => "LG", 
                    1 => "LN",
                    2 => "LE",
                    3 => "NG",
                    4 => "TN",
                    5 => "NE",
                    6 => "CG",
                    7 => "CN",
                    8 => "CE"
);

open( LFH, "> convert_514_log.txt" ) || die "Can't open log file: $!";
print LFH "PCGen 5.12 to 5.14 conversion program v0.65\n";
print LFH "Copyright (c) 2008 Chris 'Barak' Chandler\n";
print LFH "-----------------------------------------\n";
print LFH scalar localtime() . "\n\n";
foreach (@ARGV) {
    if ( $_ =~ /-S/ ) {
        open( SFH, "> shieldprof.lst" ) || die "Can't create shieldprof file: $!";
        print SFH "\nBuckler\nShield (Light)\nShield (Heavy)\nTower Shield";
        print LFH "Created generic shieldprof file";
        close(SFH);
    }
    elsif ( $_ =~ /-I/ ) {
        $input_dir = substr( $_, index( $_, "=" ) + 1 );
    }
    elsif ( $_ =~ /-O/ ) {
        $output_dir = substr( $_, index( $_, "=" ) + 1 );
    }
}
find( \&process_file, $input_dir );
print LFH "\n\n-----------------------------------------------\n";
print LFH "Conversion Completed " . scalar localtime();
close(LFH);

sub process_file {
    my $file_name;
    my $line;
    my $linecount = 0;
    my @FileArray;
    my $output_file;
    my $prof;
    my $favclass;
    my $diesizes=0;
    my $resize=0;
    my $weaponreach=0;
    my $previewdir=0;
    my $previewsheet=0;
    my @armorprofs;
    my $armorprof;
    my $armorprof_file;
    my $pcc_file;
    my $prexxx;
    my $skill;
    my $feat;
    my $count;
    my $class;
    
#####
# Process directories
#####
    if ( -d $_ ) {
        if ($_ =~ /^\./) {
            print LFH "Creating output directory structure recursively based on input structure\n";
            print LFH "------------------------------------------------------------------------\n";
            follow_dir($input_dir, \&copy_dir);
            print LFH "\n\n";
            print LFH "Processing data files (path and line # references refer to input files)\n";
            print LFH "-----------------------------------------------------------------------\n";
            $out_path = $output_dir;
        }
        else {
            $out_path = $output_dir . substr($File::Find::name, length($input_dir));
        }
    }
#####
# Process .pcc file
#####
    if ($_ =~ /pcc$/ ) {
        $file_name = $_;
        $base_name = substr($_, 0, rindex($_, "."));
        print LFH $File::Find::dir . "/$file_name \n";
        print "Processing: $file_name \n";
        open( IFH, "< $file_name" ) || die "Can't open race input file: $!";
        foreach $line (<IFH>) {
            chomp($line);
            @FileArray = ( @FileArray, $line );
        }
        close(IFH);
        $output_file = $out_path . "/" . $file_name;
        open( OFH, "> $output_file" ) || die "Can't open output file: $!";
        foreach $line (@FileArray) {
            print OFH $line . "\n";
        }
        close( OFH );
    }
#####
# Read in .lst file to be processed
#####
    if ( $_ =~ /lst$/ ) {
        $file_name = $_;
        print LFH $File::Find::dir . "/$file_name \n";
        print "Processing: $file_name \n";
        open( IFH, "< $file_name" ) || die "Can't open race input file: $!";
        foreach $line (<IFH>) {
            chomp($line);
            @FileArray = ( @FileArray, $line );
        }
        close(IFH);
#####
# Open file for output
#####
        $output_file = $out_path . "/" . $file_name;
        open( OFH, "> $output_file" ) || die "Can't open output file: $!";
#####
# Process file line by line
#####
        foreach $line (@FileArray) {
            my @fields;
            my $fieldcount = 0;
            my $field;
            my @workarray;
            my @workarray1;
            my $Temp;
            my $Temp1;
            my $followeralign=0;
            $linecount++;
            chomp($line);
    # Pass comment lines and blanks thorough as is
            if ( $line =~ /^$/ or $line eq "" or $line =~ /^#/ ) {
                print OFH $line . "\n";
                next;
            }
#####
# Split data lines into individual fields
#####
            if ($line !~ /\t/) {
                @fields = (@fields, $line);
            }
            else {
                @fields = split( '\t', $line );
                if ( $#fields <= 0 ) {
                    next;
                }
            }
#####
# Process each field in a line
#####
            foreach $field (@fields) {
            $fieldcount++;
#####
# SA to SAB conversion
#####
                if ( $field =~ /^SA:/ ) {
                    if ($field =~ /\.CLEAR/ ) {
                        print LFH "**  (Line $linecount) SA:.CLEAR is no longer valid, it needs to be updated to a SA:<blah>|PRExxx syntax\n";
                        print OFH $field;
                        if ($fieldcount <= $#fields) {
                            print OFH "\t";
                        }
                    }
                    else {
                        $field =~ s/^SA:/SAB:/g;
                        $field =~ s/PRECLASS:([A-Za-z])/PRECLASS:1,$1/g;
                        $field =~ s/PRESA:([A-Za-z])/PRESA:1,$1/g;
                        $field =~ s/PREMOVE:([A-Za-z])/PREMOVE:1,$1/g;
                        $field =~ s/PREWIELD:([A-Za-z])/PREWIELD:1,$1/g;
                        $field =~ s/PREDEITY:([A-Za-z])/PREDEITY:1,$1/g;
                        $field =~ s/PRETEMPLATE:([A-Za-z])/PRETEMPLATE:1,$1/g;
                        $field =~ s/PRERACE:([A-Za-z%])/PRERACE:1,$1/g;
                        $field =~ s/PRELEVEL:([\d*])/PRELEVEL:MIN=$1/g;
                        $field =~ s/PRELEVELMAX:([\d*])/PRELEVEL:MAX=$1/g;
                        $field =~ s/PRETYPE:([A-Za-z])/PRETYPE:1,$1/g;
                        $field =~ s/PREWEAPONPROF:([A-Za-z])/PREWEAPONPROF:1,$1/g;
                        print OFH $field;
                        if ($fieldcount <= $#fields) {
                            print OFH "\t";
                        }
                    }
                }
#####
# HD tag conversions - SA to SAB conversion
#####
                elsif ( $field =~ /^HD:/ ) {
                    if ( $field =~ /:SA:/ ) {
                        $field =~ s/SA:/SAB:/g;
                        print OFH $field . "\t";
                    }
                    else {
                        print OFH $field . "\t";
                    }
                }
#####
# LEVEL tag conversions - SA to SAB conversion
#####
                elsif ( $field =~ /^LEVEL:/ ) {
                    if ( $field =~ /:SA:/ ) {
                        $field =~ s/SA:/SAB:/g;
                        print OFH $field . "\t";
                    }
                    else {
                        print OFH $field . "\t";
                    }
                }
#####
# Remove invalid WT:- tags
#####
                elsif ( $field =~ /^WT:/ ) {
                    if ( $field =~ /-/ ) {
                    }
                    else {
                        print OFH $field . "\t";
                    }
                }
#####
# Fix invalid VISION tags (those with a comma in them)
#####
                elsif ( $field =~ /^VISION/ ) {
                    $field =~ s/,/\|/g;
                    print OFH $field . "\t";
                }
#####
# Fix invalid CLASSES tags (those with embedded PRExxxs in [])
#####
                elsif ( $field =~ /^CLASSES/ ) {
                    if ($field =~ /\[/ ) {
                        $Temp = substr($field, index($field, "["));
                        $Temp = substr($Temp, 1);
                        $Temp = substr($Temp, 0, rindex($Temp, "]"));
                        $field = substr($field, 0, index($field, "["));
                        print OFH $field . "\t";
                        print OFH $Temp . "\t";
                    }
                    else {
                        print OFH $field . "\t";
                    }
                }
#####
# PROFICIENCY to PROFICIENCY:<type>|<proficiency> conversion for weapons & shields
#####
                elsif ( $field =~ /^PROFICIENCY/ && $field !~ /WEAPON/ ) {
                    if ( grep /^TYPE:.*Weapon/, @fields ) {
                        print OFH "PROFICIENCY:WEAPON|" . substr( $field, rindex( $field, ":" ) + 1 ) . "\t";
                    }
     # If it's in any other type than weapons, strip it out, we'll recreate it later
                    else {
                    }
                }
#####
# Catch TYPE:Shield so we can add new PROFICIENCY tags for shield proficiencies
#####
                elsif ( $field =~ /^TYPE:.*Shield/  && $file_name =~ /armor/ ) {
                    if ( $field =~ /Light/ ) {
                        print OFH $field . "\tPROFICIENCY:SHIELD|Shield (Light)\t";
                    }
                    elsif ( $field =~ /Heavy/ ) {
                        print OFH $field . "\tPROFICIENCY:SHIELD|Shield (Heavy)\t";
                    }
                    elsif ( $field =~ /Buckler/ ) {
                        print OFH $field . "\tPROFICIENCY:SHIELD|Buckler\t";
                    }
                    elsif ( $field =~ /Tower/ ) {
                        print OFH $field . "\tPROFICIENCY:SHIELD|Tower\t";
                    }
                    else {
                        print OFH $field . "\t";
                        print LFH "**  (Line $linecount) Unable to determine shield proficiency\n";
                        print LFH "     TYPE tag must have one of the following: Light, Heavy, Buckler, Tower\n";
                    }
                }
#####
# Catch TYPE:Armor so we can add new PROFICIENCY tags for armor proficiencies
#####
                elsif ( $field =~ /^TYPE:.*Armor/ && $file_name =~ /armor/) {
    # Barding is special, only three prof, by weight
                    if ( $field =~ /Barding/ ) {
                        if ( $field =~ /Light/ ) {
                            print OFH $field . "\tPROFICIENCY:ARMOR|Barding (Light)\t";
                        }
                        elsif ( $field =~ /Medium/ ) {
                            print OFH $field . "\tPROFICIENCY:ARMOR|Barding (Medium)\t";
                        }
                        elsif ( $field =~ /Heavy/ ) {
                            print OFH $field . "\tPROFICIENCY:ARMOR|Barding (Heavy)\t";
                        }
                    }
    # All other armors get a proficiency using the armors name (unless a PROFICENCY tag already exists)
                    elsif (grep /PROFICIENCY/, @fields ) {
                        my $field1;
                        foreach $field1 (@fields) {
                            if ($field1 =~ /^PROFICIENCY/) {
                                print OFH "PROFICIENCY:ARMOR\|" . substr($field1, rindex($field1, ":")+1);
                                print OFH "\t" . $field;
                            }
                        }
                    }
                    else {
                        print OFH $field . "\tPROFICIENCY:ARMOR|" . $fields[0] . "\t";
                        @armorprofs = (@armorprofs, $fields[0])
                    }
                }
#####
# AUTO:ARMORPROF|TYPE to AUTO:ARMORPROF|ARMORTYPE conversion
#####
                elsif ( $field =~ /\QAUTO:ARMORPROF\E/ ) {
                    print OFH "AUTO:ARMORPROF";
                    $Temp = substr( $field, index( $field, "\|" ) + 1 );
                    @workarray = split( '\|', $Temp );
                    foreach $prof (@workarray) {
                        if ($prof =~ "TYPE" ) {
                            print OFH "|ARMORTYPE=" ;
                            if ($prof =~ "=" ) {
                              print OFH substr( $prof, rindex( $prof, "=" ) + 1 );
                            }
                            elsif ($prof =~ "\." ) {
                              print OFH substr( $prof, rindex( $prof, "." ) + 1 );
                            }
                        }
                        else {
                          print OFH "\|" . $prof;
                        }
                    }
                    print OFH "\t";
                }
#####
# AUTO:SHIELDPROF|TYPE to AUTO:SHIELDPROF|SHIELDTYPE conversion
#####
                elsif ( $field =~ /\QAUTO:SHIELDPROF\E/ ) {
                    print OFH "AUTO:SHIELDPROF";
                    $Temp = substr( $field, index( $field, "\|" ) + 1 );
                    @workarray = split( '\|', $Temp );
                    foreach $prof (@workarray) {
                        if ($prof =~ "TYPE" ) {
                            print OFH "|SHIELDTYPE=" ;
                            if ($prof =~ "=" ) {
                              print OFH substr( $prof, rindex( $prof, "=" ) + 1 );
                            }
                            elsif ($prof =~ "\." ) {
                              print OFH substr( $prof, rindex( $prof, "." ) + 1 );
                            }
                        }
                        else {
                          print OFH "\|" . $prof;
                        }
                    }
                    print OFH "\t";
                }
#####
# FEATAUTO to AUTO:FEAT conversion
#####
                elsif ( $field =~ /FEATAUTO/ ) {
                    print OFH "AUTO:FEAT|" . substr( $field, rindex( $field, ":" ) + 1 );
                    if ($fieldcount <= $#fields) {
                       print OFH "\t";
                    }
                }
#####
# BONUSFEATS -> BONUS:FEAT|POOL
#####
                elsif ( $field =~ /BONUSFEATS/ ) {
                    print OFH "BONUS:FEAT\|POOL\|" . substr( $field, rindex( $field, ":" ) + 1 ) . "\t";
                }
#####
# REPEATLEVEL to #:REPEATLEVEL conversion in class usage
#####
                elsif ( $field =~ /^[\d{1,2}]/ ) {
                    if (grep /REPEATLEVEL/, @fields ) {
                        my $field1;
                        foreach $field1 (@fields) {
                            if ($field1 =~ /^REPEATLEVEL/) {
                                print OFH $field . "\:" . $field1 . "\t";
                            }
                        }
                    }
                    else {
                        print OFH $field . "\t";
                    }
                }
    # Since we did a lookahead for REPEATLEVEL and used it, skip it when it comes up as a field
                elsif ( $field =~ /^REPEATLEVEL/ ) {
                    if ( $file_name =~ /class/ ) {
                    }
    # If in any file other than a class file, send REPEATLEVEL through as is
                    else {
                        print OFH $field . "\t";
                    }
                }
#####
# CONTAINS:-1 to CONTAINS:UNLIM
#####
                elsif ( $field =~ /CONTAINS/ ) {
                    $field =~ s/-1/UNLIM/g;
                    print OFH $field . "\t";
                }
#####
# MOVECLONE:w,x,y,z -> MOVECLONE:x,y,z
#####
                elsif ( $field =~ /MOVECLONE/ ) {
                    $Temp = substr( $field, rindex( $field, ":" ) + 1 );
                    @workarray = split( ',', $Temp );
                    if ( $workarray[1] =~ /0/ ) {
                        print OFH "MOVECLONE:" . $workarray[0] . "," . $workarray[2] . "," . $workarray[3] . "\t";
                    }
                    else {
                        print LFH "--  (Line $linecount) " . $field . " needs to be manually updated\n";
                        print OFH $field . "\t";
                    }
                }
                elsif ( $field =~ /PREHD/ ) {
                    if ( $field =~ /\+/ ) {
                        $field = substr( $field, rindex( $field, ":" ) + 1, 1 );
                        print OFH "PREHD:MIN=" . $field . "\t";
                    }
                    else {
                        $field = substr( $field, rindex( $field, ":" ) + 1 );
                        @workarray = split( '-', $field );
                        print OFH "PREHD:MIN=" . $workarray[0] . ",MAX=" . $workarray[1] . "\t";
                    }
                }
#####
# REMOVE:FEAT(<feat1>,<feat2>)# -> REMOVE:FEAT|<feat1>,<feat2>|#
#####
                elsif ( $field =~ /REMOVE\:FEAT/ ) {
                    my $extension;
                    $field = substr( $field, 11 );
                    $extension = substr( $field, rindex( $field, ")" )+1);
                    $field = substr( $field, 0, rindex( $field, ")" ));
                    $field = substr( $field, 1 );
                    print OFH "REMOVE:FEAT\|" . $field;
                    if ($extension eq "" ) {
                    print OFH "\t";
                    }
                    else {
                        print OFH "\|" . $extension . "\t";
                    }
                }
#####
# CHOICE:x to CHOICE:<type>|<typename>
#####
                elsif ( $field =~ /^CHOICE/ ) {
    # Send it as is if already in the proper format
                    if (    $field =~ /SCHOOL/ || $field =~ /SUBSCHOOL/ || $field =~ /DESCRIPTOR/ ) {
                        print OFH $field . "\t";
                    }
    # Otherwise, convert it to the proper format (if it's a legit School, Subschool or Descriptor)
                    else {
                        $field = substr( $field, rindex( $field, ":" ) + 1 );
                        if ( grep /\Q$field\E/, @schools ) {
                            print OFH "CHOICE:SCHOOL|" . $field . "\t";
                        }
                        elsif ( grep /\Q$field\E/, @subschools ) {
                            print OFH "CHOICE:SUBSCHOOL|" . $field . "\t";
                        }
                        elsif ( grep /\Q$field\E/, @descriptors ) {
                            print OFH "CHOICE:DESCRIPTOR|" . $field . "\t";
                        }
    # Remove the field and log it if it's not a legit School, Subschool or Descriptor
                        else {
                            print LFH "--  (Line $linecount) " . $field
                              . " from CHOICE tag is not a valid School, Subschool or Descriptor - removing from line\n";
                        }
                    }
                }
#####
# FAVCLASS -> FAVCLASS:<class>.<subclass>
#####
                elsif ($field =~ /^FAVCLASS/ ) {
    # If the field doesn't contain Wizard or a subclass thereof, or a Psion subclass, pass it through as is
                    if ( $field !~ /Wizard/ 
                         && $field !~ /Abjurer/ 
                         && $field !~ /Conjurer/ 
                         && $field !~ /Diviner/ 
                         && $field !~ /Enchanter/ 
                         && $field !~ /Evoker/ 
                         && $field !~ /Illusionist/ 
                         && $field !~ /Necromancer/ 
                         && $field !~ /Transmuter/ 
                         && $field !~ /Seer/      
                         && $field !~ /Shaper/    
                         && $field !~ /Kineticist/
                         && $field !~ /Egoist/    
                         && $field !~ /Nomad/     
                         && $field !~ /Telepath / 
                         || $field =~ /\./
                       ) {
                        print OFH $field . "\t";
                    }
                    else {
    # If the field does not contain multiple entries, convert the single entry
                        if ($field !~ /\|/) {
                            $Temp = substr($field, rindex($field, ":")+1);
                            print OFH "FAVCLASS:" . $subclass{$Temp} . "\t";
                        }
    # Split the field and check/convert all entries as necessary if there is more than one favored class
                        else {
                            my $favclasscount = 0;
                            print OFH "FAVCLASS:";
                            $Temp = substr( $field, index( $field, ":" ) + 1 );
                            @workarray = split( '\|', $Temp );
                            foreach $favclass (@workarray) {
                                $favclasscount++;
                                if ($favclasscount > 1) {
                                    print OFH "\|";
                                }
                                if (grep /\Q$favclass\E/, %subclass) {
                                    print OFH $subclass{$favclass};
                                }
                                else {
                                    print OFH $favclass;
                                }
                            }
                            print OFH "\t";
                        }
                    }
                }
#####
# FAVOREDCLASS -> FAVOREDCLASS:<class>.<subclass>
#####
                elsif ($field =~ /^FAVOREDCLASS/ ) {
    # If the field doesn't contain Wizard or a subclass thereof, or a Psion subclass, pass it through as is
                    if ( $field !~ /Wizard/ 
                         && $field !~ /Abjurer/ 
                         && $field !~ /Conjurer/ 
                         && $field !~ /Diviner/ 
                         && $field !~ /Enchanter/ 
                         && $field !~ /Evoker/ 
                         && $field !~ /Illusionist/ 
                         && $field !~ /Necromancer/ 
                         && $field !~ /Transmuter/ 
                         && $field !~ /Seer/      
                         && $field !~ /Shaper/    
                         && $field !~ /Kineticist/
                         && $field !~ /Egoist/    
                         && $field !~ /Nomad/     
                         && $field !~ /Telepath / 
                         || $field =~ /\./

                       ) {
                        print OFH $field . "\t";
                    }
                    else {
    # If the field does not contain multiple entries, convert the single entry
                        if ($field !~ /\|/) {
                            $Temp = substr($field, rindex($field, ":")+1);
                            print OFH "FAVOREDCLASS:" . $subclass{$Temp} . "\t";
                        }
    # Split the field and check/convert all entries as necessary if there is more than one favored class
                        else {
                            my $favclasscount = 0;
                            print OFH "FAVOREDCLASS:";
                            $Temp = substr( $field, index( $field, ":" ) + 1 );
                            @workarray = split( '\|', $Temp );
                            foreach $favclass (@workarray) {
                                $favclasscount++;
                                if ($favclasscount > 1) {
                                    print OFH "\|";
                                }
                                if (grep /\Q$favclass\E/, %subclass) {
                                    print OFH $subclass{$favclass};
                                }
                                else {
                                    print OFH $favclass;
                                }
                            }
                            print OFH "\t";
                        }
                    }
                }
                elsif ($field =~ /DIESIZES/) {
                    $diesizes = 1;
                    print OFH $field . "\t";
                }
                elsif ($field =~ /RESIZABLEEQUIPTYPE/) {
                    $resize = 1;
                    print OFH $field . "\t";
                }
                elsif ($field =~ /WEAPONREACH/) {
                    $weaponreach = 1;
                    print OFH $field . "\t";
                }
                elsif ($field =~ /PREVIEWDIR/) {
                    $previewdir = 1;
                    print OFH $field . "\t";
                }
                elsif ($field =~ /PREVIEWSHEET/) {
                    $previewsheet = 1;
                    print OFH $field . "\t";
                }
#####
# PRESPELLSCHOOL:<school>,<# req>,<lvl req> -> PRESPELLSCHOOL:<# req>,<school>=<lvl req>
#   -- single instance
#####
                elsif ($field =~ /PRESPELLSCHOOL:[A-Za-z]/ && $field !~ /\[/) {
                    $field = substr($field, rindex($field, ":")+1);
                    @workarray = split( ",", $field);
                    print OFH "PRESPELLSCHOOL:" . $workarray[1] . "," . $workarray[0] . "=" . $workarray[2] . "\t";
                }
#####
# PRESPELLSCHOOL:<school>,<# req>,<lvl req> -> PRESPELLSCHOOL:<# req>,<school>=<lvl req>
#   -- in a PREMULT
#####
                elsif ($field =~ /PRESPELLSCHOOL:[A-Za-z]/ && $field =~ /\[/) {
                    print OFH substr($field, 0, index($field, ","));
                    $field = substr($field, index($field, ",")+1);
                    $field = substr($field, 1, rindex($field, "\]")-1);
                    @workarray = split( '\],\[', $field);
                    foreach $prexxx (@workarray) {
                        print OFH ",";
                        if ($prexxx =~ /PRESPELLSCHOOL:[A-Za-z]/) {
                            $prexxx = substr($prexxx, rindex($prexxx, ":")+1);
                            @workarray1 = split( ',', $prexxx);
                            print OFH "\[PRESPELLSCHOOL:" . $workarray1[1] . "," . $workarray1[0] . "=" . $workarray1[2] . "\]";
                        }
                        else {
                            print OFH "\[" . $prexxx . "\]";
                        }
                    }
                print OFH "\t";
                }
#####
# PRESPELLSCHOOLSUB:<subschool>,<# req>,<lvl req> -> PRESPELLSCHOOLSUB:<# req>,<subschool>=<lvl req>
#   -- single instance
#####
                elsif ($field =~ /PRESPELLSCHOOLSUB:[A-Za-z]/ && $field !~ /\[/) {
                    $field = substr($field, rindex($field, ":")+1);
                    @workarray = split( ",", $field);
                    print OFH "PRESPELLSCHOOLSUB:" . $workarray[1] . "," . $workarray[0] . "=" . $workarray[2] . "\t";
                }
#####
# PRESPELLSCHOOLSUB:<subschool>,<# req>,<lvl req> -> PRESPELLSCHOOLSUB:<# req>,<subschool>=<lvl req>
#   -- in a PREMULT
#####
                elsif ($field =~ /PRESPELLSCHOOLSUB:[A-Za-z]/ && $field =~ /\[/) {
                    print OFH substr($field, 0, index($field, ","));
                    $field = substr($field, index($field, ",")+1);
                    $field = substr($field, 1, rindex($field, "\]")-1);
                    @workarray = split( '\],\[', $field);
                    foreach $prexxx (@workarray) {
                        print OFH ",";
                        if ($prexxx =~ /PRESPELLSCHOOLSUB:[A-Za-z]/) {
                            $prexxx = substr($prexxx, rindex($prexxx, ":")+1);
                            @workarray1 = split( ',', $prexxx);
                            print OFH "\[PRESPELLSCHOOLSUB:" . $workarray1[1] . "," . $workarray1[0] . "=" . $workarray1[2] . "\]";
                        }
                        else {
                            print OFH "\[" . $prexxx . "\]";
                        }
                    }
                print OFH "\t";
                }
#####
# PRESPELLDESCRIPTOR:<descriptor>,<# req>,<lvl req> -> PRESPELLDESCRIPTOR:<# req>,<descriptor>=<lvl req>
#   -- single instance
#####
                elsif ($field =~ /PRESPELLDESCRIPTOR:[A-Za-z]/ && $field !~ /\[/) {
                    $field = substr($field, rindex($field, ":")+1);
                    @workarray = split( ",", $field);
                    print OFH "PRESPELLDESCRIPTOR:" . $workarray[1] . "," . $workarray[0] . "=" . $workarray[2] . "\t";
                }
#####
# PRESPELLDESCRIPTOR:<descriptor>,<# req>,<lvl req> -> PRESPELLDESCRIPTOR:<# req>,<descriptor>=<lvl req>
#   -- in a PREMULT
#####
                elsif ($field =~ /PRESPELLDESCRIPTOR:[A-Za-z]/ && $field =~ /\[/) {
                    print OFH substr($field, 0, index($field, ","));
                    $field = substr($field, index($field, ",")+1);
                    $field = substr($field, 1, rindex($field, "\]")-1);
                    @workarray = split( '\],\[', $field);
                    foreach $prexxx (@workarray) {
                        print OFH ",";
                        if ($prexxx =~ /PRESPELLDESCRIPTOR:[A-Za-z]/) {
                            $prexxx = substr($prexxx, rindex($prexxx, ":")+1);
                            @workarray1 = split( ',', $prexxx);
                            print OFH "\[PRESPELLDESCRIPTOR:" . $workarray1[1] . "," . $workarray1[0] . "=" . $workarray1[2] . "\]";
                        }
                        else {
                            print OFH "\[" . $prexxx . "\]";
                        }
                    }
                print OFH "\t";
                }
#####
# PRESPELLTYPE:<type>,<# req>,<lvl req> -> PRESPELLTYPE:<# req>,<type>=<lvl req>
#   -- single instance
#####
                elsif ($field =~ /PRESPELLTYPE:[A-Za-z]/ && $field !~ /\[/) {
                    $field = substr($field, rindex($field, ":")+1);
                    @workarray = split( ",", $field);
                    print OFH "PRESPELLTYPE:" . $workarray[1] . "," . $workarray[0] . "=" . $workarray[2] . "\t";
                }
#####
# PRESPELLTYPE:<type>,<# req>,<lvl req> -> PRESPELLTYPE:<# req>,<type>=<lvl req>
#   -- in a PREMULT
#####
                elsif ($field =~ /PRESPELLTYPE:[A-Za-z]/ && $field =~ /\[/) {
                    print OFH substr($field, 0, index($field, ","));
                    $field = substr($field, index($field, ",")+1);
                    $field = substr($field, 1, rindex($field, "\]")-1);
                    @workarray = split( '\],\[', $field);
                    foreach $prexxx (@workarray) {
                        print OFH ",";
                        if ($prexxx =~ /PRESPELLTYPE:[A-Za-z]/) {
                            $prexxx = substr($prexxx, rindex($prexxx, ":")+1);
                            @workarray1 = split( ',', $prexxx);
                            print OFH "\[PRESPELLTYPE:" . $workarray1[1] . "," . $workarray1[0] . "=" . $workarray1[2] . "\]";
                        }
                        else {
                            print OFH "\[" . $prexxx . "\]";
                        }
                    }
                print OFH "\t";
                }
#####
# CHOOSE coversions (non equipmod)
#####
                elsif ($field =~ /^CHOOSE/ && $file_name !~ /equipmod/) {
    # CHOOSE:Langauge(language) -> CHOOSE:LANGUAGE|language
                    if ($field =~ /:Language/) {
                        $field = substr($field, rindex($field, "\(")+1);
                        $field = substr($field, 0, rindex($field, "\)"));
                        print OFH "CHOOSE:LANGUAGE\|" . $field;
                    }
                    elsif ($field =~ /Exotic$/) {
                        print OFH "CHOOSE:PROFICIENCY|WEAPON|UNIQUE|TYPE.Exotic\t";
                    }
                    elsif ($field =~ /Martial$/) {
                        print OFH "CHOOSE:PROFICIENCY|WEAPON|UNIQUE|TYPE.Martial\t";
                    }
#    # remove NUMCHOICES=# from CHOOSE:SPELLLEVEL tags
#                    elsif ($field =~ /SPELLLEVEL/ && $field =~ /NUMCHOICES/) {
#                        $field =~ s/NUMCHOICES=[\d*]\|//g;
#                        print OFH $field . "\t";
#                    }
    # split TYPE.Arcane,TYPE.Divine in SPELLLEVEL
                    elsif ($field =~ /SPELLLEVEL/ && $field =~ /,/) {
                        my $class1;
                        my $class2;
                        my $levelend;
                        $field = substr($field, index($field, "\|")+1);
                        @workarray = split( '\|', $field );
                        $field = substr($field, index($field, "\["));
                        $class1 = substr($workarray[1], 0, index($workarray[1], ","));
                        $class2 = substr($workarray[1], index($workarray[1], ",")+1);
                        $levelend = substr($workarray[3], 0, index($workarray[3], "["));
                        print OFH "CHOOSE:SPELLLEVEL\|" . $workarray[0] . "\|" . $class1 . "\|" . $workarray[2] . "\|" . $levelend . "\|" . $class2 . "\|" . $workarray[2] . "\|" . $levelend . $field . "\t";
                    }
    # CHOOSE:<blah> to CHOOSE:NOCHOICE
                    elsif ($field !~ /\|/) {
                            print OFH "CHOOSE:NOCHOICE\t";
                    }
    # remove trailing numbers and add SELECT:# (covers straight numbers and x+y formulas
    # remove invalid LIST parameter (only seems to occur in those that have the trailing #)
                    elsif ($field =~ /[\d{1,2}]$|\|\w\+\w/) {
                        my $field1;
                        my $haslist = 0;
                        $field1 = substr($field, rindex($field, "\|")+1);
                        $field = substr($field, 0, rindex($field, "\|"));
                        if ($field =~ /\|LIST/) {
                            $haslist = 1;
                            $field = substr($field, 0, rindex($field, "\|"));
                        }
                        if ( $field =~ /NONCLASSSKILLLIST/ ) {
                            $field = "CHOOSE:SKILLSNAMED|CROSSCLASS|EXCLUSIVE\t";
                        }
                        elsif ( $field =~ /CCSKILLLIST/ ) {
                            $field = "CHOOSE:SKILLSNAMED|CROSSCLASS\t";
                        }
                        elsif ($field =~ /SKILLLIST/) {
                            if ($haslist eq "1" ) {
                                $field = "CHOOSE:SKILLSNAMED|ALL\t";
                            }
                            else {
                                $field = substr($field, index($field, "\|")+1);
                                @workarray = split( ',', $field );
                                $field = "CHOOSE:SKILLSNAMED";
                                foreach $skill (@workarray) {
                                    $field = $field . "\|" . $skill;
                                }
                            }
                        }
                        print OFH $field;
                        print OFH "\tSELECT:" . $field1 . "\t";
                    }
    # CHOOSE:STRING from anything that doesn't match a documented CHOOSE subtag
                    elsif ( $field =~ /\|/ ) {
                        my $field1;
    # Split on the = if it's a FEAT parameter
                        if ( $field =~ /CHOOSE:FEAT=/ ) {
                            $field1 = substr( $field, 0, index( $field, "=" ));
                        }
    # Split between the first & Second pipes if its a NUMCHOICES parameter
                        elsif ($field =~ /CHOOSE:NUMCHOICES/ ) {
                            $field1 = substr( $field, index( $field, "\|" )+1);
                            $field1 = substr( $field1, 0, index( $field1, "\|" ));
                        }
    # Otherwise split on the first pipe
                        else {
                            $field1 = substr( $field, 0, index( $field, "\|" ));
                        }
    # Strip off the CHOOSE to get the subtag (if it hasn't been done already by the NUMCHOICES portion)
                        if ($field1 =~ /:/) {
                            $field1 = substr( $field1, index( $field, ":" )+1);
                        }
    # NONCLASSSKILLLIST conversion to CHOOSE:SKILLSNAMED
                        if ( $field1 =~ /NONCLASSSKILLLIST/ ) {
                            print OFH "CHOOSE:SKILLSNAMED|CROSSCLASS|EXCLUSIVE\t";
                        }
    # CCSKILLLIST conversion to CHOOSE:SKILLSNMAMED
                        elsif ( $field1 =~ /CCSKILLLIST/ ) {
                            print OFH "CHOOSE:SKILLSNAMED|CROSSCLASS\t";
                        }
    # if it is one of the documented subtokens, pass it on through
                        elsif (grep /\Q$field1\E/, @choose_subtokens) {
                            print OFH $field . "\t";
                        }
    # if it is not a documented subtoken, use CHOOSE:STRING
                        else {
                            if ($field =~ /CHOOSE:NUMCHOICES/ ) {
                                print OFH substr( $field, 0, index( $field, "\|" ));
                                $Temp = substr( $field, index( $field, "\|" ));
                                $Temp = substr( $Temp, index( $Temp, "\|" )+1);
                                print OFH "\|STRING\|";
                                print OFH  $Temp . "\t";
                            }
                            else {
                                print OFH "CHOOSE:STRING|" . substr($field, rindex($field, ":")+1) . "\t";
                            }
                        }
                    }
                    else {
                        print OFH $field . "\t";
                    }
                }
#####
# CHOOSE coversions (equipmod)
#####
                elsif ($field =~ /^CHOOSE/ && $file_name =~ /equipmod/) {
                    if ($field =~ /MIN=/ && $field =~ /MAX=/) {
                        $field = substr($field, rindex($field, ":")+1);
                        @workarray = split('\|', $field);
                        if (grep /SKILL/, @workarray) {
                            print OFH "CHOOSE:SKILLBONUS|" . $workarray[2] . "\|" . $workarray[3] . "\|TITLE=" . $workarray[0] . "\t";
                        }
                        elsif (grep /STAT/, @workarray) {
                            print OFH "CHOOSE:STATBONUS|" . $workarray[2] . "\|" . $workarray[3] . "\|TITLE=" . $workarray[0] . "\t";
                        }
                        else {
                            print OFH "CHOOSE:NUMBER|" . $workarray[1] . "\|" . $workarray[2]  . "\|TITLE=" . $workarray[0] . "\t";
                        }
                            
                    }
                    elsif ($field =~ /\|/ && $field !~ /EQBUILDER/) {
                        $field = substr($field, rindex($field, ":")+1);
                        print OFH "CHOOSE:STRING" . substr($field, index($field, "\|"));
                        print OFH "\|TITLE=" . substr($field, 0, index($field, "\|")). "\t";
                    }
                    else {
                        print OFH $field . "\t";
                    }
                }
#####
# ADD:FEAT(blah,blah1)# -> ADD:FEAT|#|blah,blah1
#####
                elsif ($field =~ /ADD\:FEAT\(/) {
                    $field = substr($field, index($field, "(")+1);
                    my $choices = substr($field, rindex($field, ")")+1);
                    $field = substr($field, 0, rindex($field, ")"));
                    @workarray = split(',', $field);
                    $count=0;
                    print OFH "ADD:FEAT\|" . $choices . "\|";
                    foreach $feat (@workarray) {
                        if ($count > 0) {
                            print OFH ",";
                        }
                        print OFH $feat;
                        $count++;
                    }
                    print OFH "\t";
                }
#####
# ADD:CLASSSKILLS(blah,blah1)# -> ADD:CLASSSKILLS|#|blah,blah1
#####
                elsif ($field =~ /ADD\:CLASSSKILLS\(/) {
                    $field = substr($field, index($field, "(")+1);
                    my $choices = substr($field, rindex($field, ")")+1);
                    $field = substr($field, 0, rindex($field, ")"));
                    @workarray = split(',', $field);
                    $count=0;
                    print OFH "ADD:CLASSSKILLS\|" . $choices . "\|";
                    foreach $skill (@workarray) {
                        if ($count > 0) {
                            print OFH ",";
                        }
                        print OFH $skill;
                        $count++;
                    }
                    print OFH "\t";
                }
#####
# ADD:SPELLCASTER(blah,blah1)# -> ADD:SPELLCASTER|#|blah,blah1
#####
                elsif ($field =~ /ADD\:SPELLCASTER\(/) {
                    $field = substr($field, index($field, "(")+1);
                    my $choices = substr($field, rindex($field, ")")+1);
                    $field = substr($field, 0, rindex($field, ")"));
                    @workarray = split(',', $field);
                    $count=0;
                    print OFH "ADD:SPELLCASTER\|" . $choices . "\|";
                    foreach $class (@workarray) {
                        if ($count > 0) {
                            print OFH ",";
                        }
                        print OFH $class;
                        $count++;
                    }
                    print OFH "\t";
                }
#####
# FOLLOWERALIGN -> PREALIGN
#####
                elsif ($field =~ /FOLLOWERALIGN/) {
                    my $i;
                    $field = substr($field, index($field, ":")+1);
                    for ($i=0; $i <= length($field)-1; $i++) {
                        @workarray = (@workarray, substr($field, $i, 1));
                    }
                    $Temp = "PREALIGN:";
                    $count = 0;
                    foreach $i (@workarray) {
                        if ($count >= 1) {
                            $Temp = $Temp . ",";
                        }
                        
                        $Temp=$Temp . $alignments{$i};
                        $count++;
                    }
                    if (grep /DOMAINS/, @fields ) {
                        my $field1;
                        $followeralign = 1;
                        foreach $field1 (@fields) {
                            if ($field1 =~ /^DOMAINS/) {
                                print OFH $field1 . "|" . $Temp . "\t";
                                
                            }
                        }
                    }
                    else {
                            print OFH $Temp . "\t";
                    }
                }
    # drop the DOMAINS tag since we processed it with FOLLOWERALIGN
                elsif ($field =~ /DOMAINS/ && $followeralign == 1) {
                }
                else {
#####
# ADD:Language to ADD:LANGUAGE
#####
                        $field =~ s/ADD:Language/ADD:LANGUAGE/g;
#####
# PRECLASS to PRECLASS:#,y,y conversion
#####
                        $field =~ s/PRECLASS:([A-Za-z])/PRECLASS:1,$1/g;

#####
# PRESA to PRESA:#,y,y conversion
#####
                        $field =~ s/PRESA:([A-Za-z])/PRESA:1,$1/g;

#####
# PREMOVE to PREMOVE:#,y,y conversion
#####
                        $field =~ s/PREMOVE:([A-Za-z])/PREMOVE:1,$1/g;
#####
# PREMOVE to PREMOVE:#,y,y conversion
#####
                        $field =~ s/PREWIELD:([A-Za-z])/PREWIELD:1,$1/g;
#####
# PREDEITY to PREDEITY:#,y,y conversion
#####
                        $field =~ s/PREDEITY:([A-Za-z])/PREDEITY:1,$1/g;
#####
# PRETEMPLATE to PRETEMPLATE:#,y,y conversion
#####
                        $field =~ s/PRETEMPLATE:([A-Za-z])/PRETEMPLATE:1,$1/g;
#####
# PRERACE to PRERACE:#,y,y conversion
#####
                        $field =~ s/PRERACE:([A-Za-z%])/PRERACE:1,$1/g;
#####
# PRELEVEL to PRELEVEL:MIN=#
#####
                        $field =~ s/PRELEVEL:([\d*])/PRELEVEL:MIN=$1/g;
#####
# PRELEVELMAX to PRELEVEL:MAX=#
#####
                        $field =~ s/PRELEVELMAX:([\d*])/PRELEVEL:MAX=$1/g;
#####
# PRETYPE:x -> PRETYPE:#,x
#####
                        $field =~ s/PRETYPE:([A-Za-z])/PRETYPE:1,$1/g;
#####
# PREWEAPONPROF:x -> PREWEAPONPROF:#,x
#####
                        $field =~ s/PREWEAPONPROF:([A-Za-z])/PREWEAPONPROF:1,$1/g;
#####
# ADD:SA -> ADD:SAB
#####
                    $field =~ s/ADD:SA/ADD:SAB/;
#####
# KNOWNSPELLS:.CLEAR<stuff> -> KNOWNSPELLS:.CLEAR|<stuff>
#####
                    $field =~ s/^KNOWNSPELLS:.CLEAR/KNOWNSPELLS:.CLEAR\|/;
#####
# SPELLS:xxx|TIMES=-1 to SPELLS:xxx|TIMES=ATWILL conversion
#####
                    $field =~ s/TIMES=-1/TIMES=ATWILL/g;
                    print OFH $field;
                    if ($fieldcount <= $#fields) {
                        print OFH "\t";
                    }
                }
            }
            print OFH "\n";
        }
#####
# miscinfo.lst additions
#####
        if ($file_name eq "miscinfo.lst" && $diesizes != 1) {
            print OFH "\n\# DIESIZES values are used by the HITDIE tag to bump up/down HD per level.\n";
            print OFH "DIESIZES:1,2,3,MIN=4,6,8,10,MAX=12,20,100,1000\n";
        }
        if ($file_name eq "miscinfo.lst" && $resize != 1) {
            print OFH "\n\# Types which will be resized when Automatic Resizing is turned on.\n";
            print OFH "RESIZABLEEQUIPTYPE:Shield|Weapon|Armor|Ammunition|Resizable\n";
        }
        if ($file_name eq "miscinfo.lst" && $weaponreach != 1) {
            print OFH "\n\# Reach mode formula to calculate weapon reach for a player\n";
            print OFH "\# RACEREACH = player's natural reach\n";
            print OFH "\# REACH = equipment's reach\n";
            print OFH "\# REACHMULT = equipment's reach multiple\n";
            print OFH "WEAPONREACH:(RACEREACH+(max(0,REACH-5)))*REACHMULT\n";
        }
        if ($file_name eq "miscinfo.lst" && $previewdir != 1) {
            print OFH "\n\#PREVIEWDIR:d20/fantasy";
        }
        if ($file_name eq "miscinfo.lst" && $previewsheet != 1) {
            print OFH "\n\#PREVIEWSHEET:preview.html\n";
        }
        close(OFH);
    }
#####
# Create armorprofs file
#####
    if ($#armorprofs > 0) {
        print "    Creating " . $base_name . "_armorprof.lst file\n";
        $armorprof_file = $out_path . "/" . $base_name . "_armorprof.lst";
        open( AFH, "> $armorprof_file" ) || die "Can't create armorprof file: $!";
        foreach $armorprof (@armorprofs) {
            print AFH $armorprof . "\n";
        }
        if ($base_name =~ "phb_d20_fantasy_v35e" || $base_name =~ "aesrd_ogl_fantasy" || $base_name =~ "ausrd_ogl_fantasy" || $base_name =~ "rsrd_d20_fantasy_v35e" || $base_name =~ "srd_d20_fantasy_v30e") {
            print AFH "Barding (Light)\nBarding (Medium)\nBarding (Heavy)";
        }
        close(AFH);
        print LFH "--  Created " . $base_name . "_armorprof.lst file\n";
        $pcc_file = $out_path . "/" . $base_name . "\.pcc";
        print "    Adding " . $base_name . "_armorprof.lst to .pcc file\n";
        print LFH "--  Added " . $base_name . "_armorprof.lst to .pcc file\n";
        open( PFH, ">> $pcc_file" ) || die "Can't open .pcc to modify: $!";
        print PFH "\nARMORPROF:" . $base_name . "_armorprof.lst\n";
        close( PFH );
    }
}

sub follow_dir {
    my ($path, $code_ref) = @_;
    my ($DH, $file);
    if (-d $path) {
        $code_ref->($path);
        unless (opendir $DH, $path) {
            warn "Couldn't open $path.\n$!\nSkipping!";
            return;
        }
        while ($file = readdir $DH) {
            next if $file eq '.' || $file eq '..';
            follow_dir("$path/$file", $code_ref);
        }
    }
}

sub copy_dir {
    my $copy = $_[0];
    substr($copy, 0, length($input_dir), $output_dir);
    unless (-d $copy) {
        if (mkdir $copy) {
            print LFH "$copy   ---   created\n";
        } 
        else {
            print LFH "$copy   ---   Unable to create\n$!\n";
        }
    } 
    else {
        print LFH "$copy   ---   already exists - skipping\n";
    }
}
