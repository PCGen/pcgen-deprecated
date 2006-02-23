#!/usr/bin/perl

# * Copyright

# Copyright 2003 by Éric Beaudoin <beaudoer@videotron.ca>.

# All rights reserved.  You can redistribute and/or modify
# this program under the same terms as Perl itself.

# See <http://www.perl.com/perl/misc/Artistic.html>.

# This script convert path and name information within PCGEN files
# that were created before PCGEN 4.3.3


use strict;
$^W=1;            # use warnings;

# Version information
my $CVS_id = '$Id: pcgconvert.pl,v 1.3 2003/02/27 07:59:03 Éric Beaudoin Exp $';
my ($CVS_build,$CVS_date) = ($CVS_id =~ m!v \d\.(\d+) (\d{4}/\d\d/\d\d)!);
$CVS_date =~ tr!/!.!;
my $VERSION = "1.00 (build $CVS_build)";
my $VERSION_DATE = $CVS_date;
my $LongVersion = "$0 version: $VERSION -- $VERSION_DATE";
my ($SCRIPTNAME) = ($0 =~ m!([^/\\]*)$!);

print STDERR "$LongVersion\n";

use Getopt::Long;
use FileHandle;
use Pod::Html ();           # We do not import any function for
use Pod::Text ();           # the modules other than Getopt::Long
use Pod::Usage ();
use File::Find ();
use File::Basename ();
#use Text::Balanced ();

######################################################################################
######################################################################################
###
### Variables definitions

my %srd_weapon_name_convertion_433 = (
    'Sword (Great)'                 => 'Greatsword',
    'Sword (Long)'                  => 'Longsword',
    'Dagger (Venom)'                => 'Venom Dagger',
    "Dagger (Assassin's)"           => "Assassin's Dagger",
    'Mace (Smiting)'                => 'Mace of Smiting',
    'Mace (Terror)'                 => 'Mace of Terror',
    'Greataxe (Life-Drinker)'       => 'Life Drinker',
    'Rapier (Puncturing)'           => 'Rapier of Puncturing',
    'Scimitar (Sylvan)'             => 'Sylvan Scimitar',
    'Sword (Flame Tongue)'          => 'Flame Tongue',
    'Sword (Planes)'                => 'Sword of the Planes',
    'Sword (Luck Blade)'            => 'Luck Blade',
    'Sword (Subtlety)'              => 'Sword of Subtlety',
    'Sword (Holy Avenger)'          => 'Holy Avenger',
    'Sword (Life Stealing)'         => 'Sword of Life Stealing',
    'Sword (Nine Lives Stealer)'    => 'Nine Lives Stealer',
    'Sword (Frost Brand)'           => 'Frost Brand',
    'Trident (Fish Command)'        => 'Trident of Fish Command',
    'Trident (Warning)'             => 'Trident of Warning',
    'Warhammer (Dwarven Thrower)'   => 'Dwarven Thrower',
  );



#######################################################################################
#######################################################################################
## First, lets get the script parameters

my %cl_options = (
  inputpath     => "",        # Input path were the .PCG are located
  outputpath    => "",        # Output path were the new .PCG will be build
  help          => 0,         # Need instruction ?
  error_file    => "",        # Redirect STDERR to this file
   );

my $error_message = "\n";

if(scalar @ARGV)
{
    &GetOptions(
        "help|h|?"          => \$cl_options{help},
        "inputpath|i=s"     => \$cl_options{inputpath},
        "outputpath|o=s"    => \$cl_options{outputpath},
        "outputerror|e=s"   => \$cl_options{error_file},
    );

    # Print message for unknown options
    if (scalar @ARGV)
    {
        $error_message = "\nUnknown option:";

        while (@ARGV)
        {
            $error_message .= " ";
            $error_message .= shift;
        }
        $error_message .= "\n";

        $cl_options{help} = 1;
    }
}
else
{

    $cl_options{help} = 0;
}

#####################################
# Verify if the inputpath was given

if(!$cl_options{inputpath})
{
  $error_message .= "\nThe -inputpath parameter must be specified.";
  $cl_options{help} = 1;
}
elsif(!-d $cl_options{inputpath})
{
  $error_message .= "\nThe directory $cl_options{inputpath} does not exists.";
  $cl_options{help} = 1;
}
else
{
  $cl_options{inputpath} =~ tr !\\!/!;
}

#####################################
# Verify if the outputpath was given

if(!$cl_options{outputpath})
{
  $error_message .= "\nThe -outputpath parameter must be specified.";
  $cl_options{help} = 1;
}
elsif(!-d $cl_options{outputpath})
{
  $error_message .= "\nThe directory $cl_options{outputpath} does not exists.";
  $cl_options{help} = 1;
}
else
{
  $cl_options{outputpath} =~ tr !\\!/!;
}

#####################################
# Diplay usage information

if ($cl_options{help} or $Getopt::Long::error)
{
    Pod::Usage::pod2usage(
              {-msg => $error_message,
               -exitval => 1,
               -output => \*STDERR});
    exit;
}

#####################################
# Redirect STDERR if needed

if($cl_options{error_file})
{
  open NEWSTDERR, ">$cl_options{error_file}" or die "Can't create $cl_options{error_file}: $!";
  *STDERR = *NEWSTDERR;
}

######################################################################################
######################################################################################
###
### The processing starts here

#####################################
# First, we find the .PCG files

my @Files_List;

  sub mywanted{
    push @Files_List, $File::Find::name
      if !-d && /\.pcg$/i;
  };

File::Find::find(\&mywanted, $cl_options{inputpath});

#####################################
# We read each one of them.

for my $filename (sort @Files_List)
{
  $filename =~ tr!\\!/!;

  my @newlines;
  my $mustwrite = 0;

  open PCG, $filename or die "Can't open $filename: $!";

  print STDERR "Reading: $filename\n";

  # The first line has the PCGVERSION, it must be 2.0
  my $line = <PCG>;

  if($line =~ /PCGVERSION:(.*)/ && $1 == 2)
  {
    push @newlines, $line;

    # Now we find the lines that need to be converted
    while($line = <PCG>)
    {
      # skip empty lines and comments
      next if $line =~ /^\#/;

      # Let's get the tag
      my $value;
      ($_, $value) = split /:/,$line,2;

      if(/EQUIPNAME/ || /EQUIPSET/ || /FEAT/ || /WEAPONPROF/)
      {
        for my $oldname (keys %srd_weapon_name_convertion_433)
        {
          if($line =~ s(\Q$oldname\E)($srd_weapon_name_convertion_433{$oldname})ig)
          {
            print STDERR qq[  Line $. ($_): replacing "$oldname" with "$srd_weapon_name_convertion_433{$oldname}"\n];
          }
          $mustwrite = 1;
        }
      }

    } continue { push @newlines, $line; }

  }
  else
  {
    print STDERR "  Unrecognised PCGVERSION, file will not be converted\n";
  }

  close PCG or die "Can't close $filename: $!";

  # do we have something to write back?
  if($mustwrite)
  {
    # Lets construct the new output path
    my $newpath = $filename;
    $newpath =~ s/\Q$cl_options{inputpath}\E/$cl_options{outputpath}/i;
    print STDERR qq[  Writing: $newpath\n];

    # Create the directory if it doesn't already exists
    create_dir(File::Basename::dirname($newpath),$cl_options{outputpath});

    # Create and write the file
    open NEWFILE, ">$newpath" or die "Can't create $newpath: $!";

    print NEWFILE @newlines;

    close NEWFILE or die "Can't close $newpath: $!";
  }

}


#####################################
# Close error file when done

if($cl_options{error_file})
{
  close NEWSTDERR or die "Can't close $cl_options{error_file}: $!";
}

###############################################################
# create_dir
# ----------
#
# Create any part of a subdirectory structure that is not
# already there.

sub create_dir($$)
{
  my ($dir,$outputdir) = @_;

  # Only if the directory doesn't already exist
  if(!-d $dir)
  {
    my $parentdir = File::Basename::dirname($dir);

    # If the $parentdir doesn't exist, we create it
    create_dir($parentdir,$outputdir)
      if($parentdir ne $outputdir && !-d $parentdir);

    # Create the curent level directory
    mkdir $dir, 0755 or die "Cannot create directory $dir: $!";
  }
}


__END__

=head1 NAME

pcgconverter.pl -- Convert path and names for PCGEN version 4.3.3

Version: 1.00

=head1 DESCRIPTION

B<pcgconverter.pl> will attemp to convert all the .PCG (PCGEN character files) found
in a directory. The big change for with PCGEN 4.3.3 is that the core files i.e. the
SRD have been move to another directory. At the same time, some names were changed.

The directories are never listed in the PCG file but since I was doing convertion code
for the LST files, I thought it would be nice to do something for the PCG as well.

=head1 INSTALLATION

=head2 Get Perl

I'm using ActivePerl v 5.6.1 (build 635) but any standard distribution with version 5.5 and
over should work.

You can get Perl here <L<http://www.activestate.com/Products/ActivePerl/>> or here <L<http://www.cpan.org/ports/index.html>>.

=head2 Put the script somewhere

Once Perl is installed on your PC, you just have to find a home for the script. After that,
all you have to do is type B<perl pcgconvert.pl> with the proper parameters to make it
work.

=head1 SYNOPSIS

  # parse all the files in PATH, create the new ones in NEWPATH and display
  # the errors in ERROR_FILE
  perl pcgconvert.pl -inputpath=<PATH> -outputpath=<NEWPATH> -outputerror=<ERROR_FILE>

  # same as last line but using the short parameters
  perl pcgconvert.pl -i=<PATH> -o=<NEWPATH> -e=<ERROR_FILE>

  # display the usage guide lines
  perl pcgconvert.pl -help

  # display the full instructions
  perldoc pcgconvert.pl

=head1 PARAMETERS

=over 8


=item B<-inputpath> or B<-i>

Path to an input directory that will be scanned for .pcg files. This parameter is B<mandantory>.

=item B<-outputpath> or B<-o>

B<-outputpath> define where the new files will
be writen. The directory tree from the B<-inputpath> will be reproduce as well. This parameter is B<mandantory>.

Note 1: the output directory must be created before calling the script.

Note 2: only the files that need convertion will be written back.

=item B<-outputerror> or B<-e>

Redirect STDERR to a file. All the warning and errors found by this script are printed
to STDERR.

=item B<-help>, B<-h> or B<-?>

Print a brief help message and exits.

=back

=head1 MANIFEST

The distribution of this script includes the following files:

=over 8

=item * pcgconvert.pl

The script itself.

=item * doc.html

HMTL version of the perldoc for the script.

=item * pcgconvert.css

Style sheet files for doc.html

=item * pcgconvert.pl.sig

PGP signature for the script (for those who care about stuf like that). You can get a copy of my
key here: <L<http://pgp.mit.edu:11371/pks/lookup?op=get&search=0xDF3D914E>>

=back

=head1 COPYRIGHT

Copyright 2003 by E<Eacute>ric E<quot>Space MonkeyE<quot> Beaudoin -- <mailto:beaudoer@videotron.ca>

All rights reserved.  You can redistribute and/or modify
this program under the same terms as Perl itself.

=head1 VERSION HISTORY

=head2 v1.00 -- 2002.02.25

First release. Will convert PCG from version 4.3.x to 4.3.3.
