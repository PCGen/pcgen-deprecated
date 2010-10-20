ParseCasterLevel.pl is a utility that you can use on PCGen spell LST files (or 
any file with spell descriptions). It converts text of the type "n/level" to 
use "CASTERLEVEL*n". 

After running this utility, you should review the changes since sometimes the
text is converted incorrectly (like if a unit of value was left out). Also
look for TODO comments which are added when a change is identified, but it is
not clear how to properly parse the text.

To use this script, you need to have perl installed (I've only tested with perl
"v5.8.0 built for cygwin-multi-64int" so no guarantees how it works with other 
builds). From the command-line, you would use the script like so

   "perl ParseCasterLevel.pl <raw_spells.lst >spells.lst

where "raw_spells.lst" is the original spell data set and "spells.lst" is the 
converted data set.

Please send feedback and bug reports to eballot@gmail.com. If you are reporting
a bug, please include the line (or lines) of text from the input file so that I
can duplicate the problem.

If you update the ParseCasterLevel.pl, please send me a copy. You can use 
test.bat to validate your changes haven't had side effects. It will run
ParseCasterLevel.pl on test_in.dat and create test_out.dat. You can then diff
test_out.dat with test_out_baseline.dat. The two should be the same except for
any intentional changes you made.

Release Notes
0.1b - Processes most patterns of "#/level", including spelled out numbers.
       Handles some patterns of "maximum of #" and marks unhandled at TODO
       Handles "above nth" and "beyond nth" patterns
0.1c - Pluralizes many phrases "one creature/level" -> "(CASTERLEVEL*1) creatures"
       Fix problems reading "max" and "above" across tabs
       Add "floor" to equations so "x/y levels" is calculated correctly.

