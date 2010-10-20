FUNCTION:
========
Update PCGen race files from a single race file to a new race file and a kit

INSTALLATION
============
A) Get Perl if you don't already have it
      I'm using ActivePerl v5.10 (build 1002) but any standard distribution with version 5.8 and over should work

B) Put the script somewhere
     Once Perl is installed on your computer, you just have to find a home for the script.

C) Run the script
     All you have to do is type "perl convert_monsters.pl" with the proper parameters to make it work. (However, I would recommend using the batch file as noted below).

COMMAND LINE PARAMETERS:
========================
-<filename> --> file name (with path if not in the same directory)

USAGE NOTES:
======

1)  The script will create two new files for each race file found.  One will be a new race file with the same name as the previous file but with"_new" appended to the end (but before the .lst extension).  This file will have all of the old tags with a PREDEFAULTMONSTER tag attached removed, as well as the other soon to be obsolete default monster tags.  The second file will be a kit file with the same name as the original file (less the _race part) and with _kit appended (again before the .lst extension).  The kit names will be the monster name with " ~ Default" appended to the end.  You may change this by opening the .pl file and editing the text in quotes on line 42.

2) The script will open the .pcc file and comment out the original file name and add entries for the new race file and the new kit file. (Thus, if you have issues, you can easily revert to the old way by commenting out the two new lines and uncommenting the original race line).

3) The enclosed batch file for windows will start in the directory you specify and recursively work it's way through it and all sub-directories, processing any race and .pcc files it finds along the way.  "convert_monsters c:\pcgen" would start processing in the c:\pcgen directory and run through every sub-directory from there.

HISTORY:
========

v 1.1 -- Chris Chandler
-- fixed stat output when there was no stat with a PREDEFAULTMONSTER
-- added .pcc modification
-- re-enabled modified race file output
-- added checking so _mod_ files are not processed
-- fixed |PREALIGN: vs PREALIGN: issue

v 1.0 -- Aaron Divinsky
-- basic functionality
