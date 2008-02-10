To Use Prettylst

Use must install PERL on your machine

Default is C:\ on windows computer

--
Ewarn.pm must be placed in your PERL\lib directory

To get 4387 to work you have to download a second file (Ewarn.pm) and place it in your \Perl\lib directory.

Default Installation would be
C:\Perl\lib

An Alternate method is:
 /System/Library/Perl/5.8.6/Ewarn.pm


---

Helpful Notes for PL - using a Bat File:
NOTE: This will assume default pl folder is root of C:\

Make two folders within the 'pl' folder called 'test' and 'checked'

Place the pcc and lst files you wish to check in your test folder and then run your bat file.

Here is a sample Bat file:

perl prettylst.pl -i=c:\pl\test -o=c:\pl\checked -x -nw -nojep -e=error.txt


Good Luck and Happy coding.