Author: Terrence FitzSimons
Date: 8 December 2006

Instructions for setting up the TextPad System to use PcGen's Syntax file found here at http://groups.yahoo.com/group/pcgen_experimental/ in the folder Application Support, as well at the SVN repository at https://svn.sourceforge.net/svnroot/pcgen/Trunk/utilities/editor syntax (include the last word "syntax" as its part of the location.

First, if you don't have it, go to http://www.textpad.com/index.html and download the version that you need and the dictionary that you need.

Second, install the program where you want and use the instructions to setup the dictionary.

Third, download the PcGen's Syntax file from the files.  Place the file in the directory Samples, under TextPad.

Now for the hard part.  Start TextPad.  Under Configure tab is the "New Document Class". In the dialogue boxes that follow enter:

PcGen
*.pcc, *.lst
Enable Syntex Highlighting and select the PcGen.syn Syntex Highlighting file
Click Finish

Go back under Configure and open the Preferences tab.  Open Document Classes and click on the PcGen entry.

First page, click on the "Maintain Indention", "Strip training spaces when saving", "Save with no breaks in lines", "Check spelling of everything", Default Encoding as ANSI, Save as PC.

Jump to Colors age and change the keywords to what you like, but "Keyword 6" needs to be red background and green letter, or something that stands out.  Keyword 6 is the "deprecate or incorrect words" if you have red backlit 
words you are using something your not suposed to be using.  Check the docs directory for more information, or check the trackers page at https://sourceforge.net/projects/pcgen/

Open a PcGen file and see what Syntex Highlighting does for you.


