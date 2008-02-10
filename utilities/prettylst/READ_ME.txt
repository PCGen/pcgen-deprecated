-- To Use Prettylst --

WINDOWS INSTRUCTIONS:

You must have Perl on your machine or you will need to install it. 

Default installation is C:\ on a windows computer

I recommend Active State Perl which can be found:

  http://www.activestate.com/store/activeperl/

5.8 or higher is fine.

After Installation you will need to load the Perl Package Manager (PPM) either in the start menu or using the RUN command.
  You will need to install the "Readonly" module.
  Type in the Search Bar 'READONLY' and then mark it for install... After that is installed you're set to go.



--- PRETTYLST 4387 or Higher ---
To get Prettylst version 4387 to work you have to download a second file 'Ewarn.pm' [Included in the prettylst folder] and place it in your \Perl\lib directory.
"Ewarn.pm must be placed in your PERL\lib directory"

Default Installation would be
C:\Perl\lib

An Alternate method is:
 /System/Library/Perl/5.8.6/Ewarn.pm
NOTE: The file path would need to be resolved to the current version YOU ARE USING.



--- BAT File Help ---

Helpful Notes for PL - using a Bat File:
NOTE: This will assume default 'pl' folder is root of C:\

Make two folders within the 'pl' folder called 'test' and 'checked'

Place the pcc and lst files you wish to check in your test folder and then run your bat file.

Here is a sample Bat file:

perl prettylst.pl -i=c:\pl\test -o=c:\pl\checked -x -nw -nojep -e=error.txt

###
Here is a modified form - USE AT OWN RISK

perl prettylst.pl -i=d:\pcgen\pcgen_dev\Trunk\pcgen\data -o=c:\pl\checked -x -nw -nojep -e=error.txt

This would run prettylst on the entire data folder in the trunk... This is just an example of how you can customise your bat file. NOTE:You would actually have to setup a folder in a D:\ for this to work.


Good Luck and Happy coding.

If you need further assistance, please ask on the PCGen Y! group [http://games.groups.yahoo.com/group/pcgen/]

Created by: Andrew Maitland on 2008-02-10 
[Year-Month-Day]
