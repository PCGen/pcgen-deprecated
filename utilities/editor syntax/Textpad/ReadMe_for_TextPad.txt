
Once you have TextPad installed, get "PCGen.syn" to be copied into
the "Textpad/system" directory.  After that you need to create a document type for PCGen.

1) Start TextPad
2) Configure -> New Document Class
3) Enter "PCGen" for the name and click the Next button.
4) For the class members prompt, enter "*.lst" and "*.pcc" then click the Next button
5) Check the "Enable syntax highlighting box"
6) Use the dropdown box to select the PCGen.syn file and then click the Next button
7) Click the Finish button

Now you need to make some further adjustments for your new document type.

1) Configure -> Preferences -> Document Classes -> PCGen
2) Font: Set the font to a monospace font so everything will line up nicely (I use Courier)
3) Tabulation: Set the default tab spacing to 6 spaces and set the Indent size to 6 spaces

After this you need to make some further adjustments to the general settings of TextPad

1) Configure -> Preferences

2) View - Make sure the following are checked:
   A)  Highlight the line containing the cursor (*extremely* helpful)
   B)  Horizontal scroll bar
   C)  Line numbers (makes talking to each other about where to find stuff easier)
   D)  Vertical scroll bar
   E)  Visible white space:Paragraphs, Spaces, Tabs

3) Associated Files - add "*.lst" and "*.pcc" so that Explorer will automatically start Textpad when you
double click those file types.

4) File Name Filters
   A)  Click the New button
   B)  Enter a name/description ("PCGen" works well here)
   C)  Enter our two favorite wildcards "*.lst" and "*.pcc" separated by a comma
   D)  Click Apply

Ok, that's pretty much it for getting TextPad set up.  You'll also want to turn off word wrapping (the
.lst file lines can get really long and trying to read the word-wrapped is a quick route to insanity...)

Jump to Colors age and change the keywords to what you like, but "Keyword 6" needs to be red background
and green letter, or something that stands out.  Keyword 6 is the "deprecate or incorrect words" if you
have red backlit words you are using something your not supposed to be using.


