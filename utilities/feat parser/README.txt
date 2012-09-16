# created by Brad Kester aka tripleduck 20120912

   This utility is meant to help parse copy/pasted feats from a new source book into the dataset friendly output.
   I've only tested it with Pathfinder Advanced Race Guide on a linux system.  Likely some tweaks will have to
be made to work in Windows with ActivePerl. 

  The primary script to run is "featParser.pl fileName"

  It uses featClass.pm as the class object for a basic feat read from the given fileName.
  It uses featsSet.pm as a front-end for housing a collection of featClass objects, and being able to manage them
(a little bit).

  The parser uses a rules file to help determine how to understand the prerequisites. The file must be named
  'featParse.rules'.  It contain complex rulesets.

  Also included is the ARG feats text file used as the source material. It's only included to show an example of
what the layout can look like. 

While I tried to break out materials as best I could for ease of updating, there are definitely some tweaking
done to prereq lines to make them more friendly for the parser.  To locate the main place that this is accomplished,
search for TWEAKS in the rules file.  It's down in the parsePrereq() function. Usually, tweaks need to be set when
prereq lines include an 'or' multiple choice list, but don't split the items out correctly. For example, 
Elemental Jaunt in the ARG says:
Prerequisites: Character level 15th, ifrit, oread, sylph, or undine.

  That 'or' will cause the parser to think CL 15 is one option.  The tweak in the rules file says:
  FROM ", ifrit, oread, sylph, or undine"
  TO "; ifrit, oread, sylph, or undine"

  This changes the first , to a ;, so that the parser will know to split the line there first.

  Below is a listing of all tags and their use in the rules file.

  Please feel free to help update the script to perform better and more effectively!  Unfortunately, I know very
little about perl coding in Windows, so I don't really know what'll have to be done to make it friendlier.

Also, when there's an error identifying something in the feats file, it will stop and print out some basic info about
the problem.

Hope this helps!!

---------------------------------------
rules file tags

KEY                       This is a special keyword used to indicate a LIST match. It can be used in 
                        MATCHES as well as OUTPUT. If MATCHES contains KEY, OUTPUT must as well, unless
                        NOKEY is used (see below).
VALUE                     This is a special keyword used to indicate a numeric value. It can be used
                        in MATCHES as well as OUTPUT.
 
SET xxxx                  This begins a new rule set. Any non-SET, non-LIST, non-TWEAKS entries will be
                        assigned to the preceding SET. This is required for each type of set.
  IGNORECASE              Tells the parser that this SET does not need to match case when searching for
                        matching prerequisite entries.
  MATCHES ~               This tells the SET how to match. Can include hard-coded string data, the KEY
                        and/or VALUE special keywords, or grouped pattern matching via [] with a pipe (|)
                        delimited list. DO NOT USE () PATTERN MATCHING! ()'s are treated literally.
                        Multiple MATCHES can be used for alternative matching parameters for a set.
  OUTPUT [NOKEY] ~        This tells the SET how to display the matched information, if found. This can
                        include the KEY and/or VALUE special keywords. If NOKEY is used, a matched KEY
                        doesn't have to be in the output. Multiple OUTPUT lines will provide multiple
                        output - all are used.
  TOKEN ~                 Sets the dataset token used as a prerequisite entry in the output (such as
                        PRECLASS, PRESTAT, etc)
  COUNTPREMULT            Tells the parser whether multiple entries are counted and that number is used
                        to determine how many of that type must match. If not set and more than one entry
                        is given for a type, will only require 1 of that TOKEN.
  JOIN                    Tells the parser to join all entry matches for the given type onto one TOKEN.
                        If not used, each entry will be given it's own prereq line.
  UCASEFIRST              Tells the parser to uppercase the first word in all KEY output
  LOWERCASE               Tells the parser to lowercase all KEY output
  UPPERCASE               Tells the parser to uppercase all KEY output
  KEYLEN x                Limits the number of characters the KEY can output. If the KEY is longer than this
                        value, it will be truncated. If it is shorter, no changes occur.
  DEFAULT                 One SET in the rules list must be the default set for any matching prerequisite
                        feats in the scanned feats file (i.e. not included in the rules). This determines
                        how those items are treated. Typically it will be a SET that looks for feats.

LIST xxxx                 Sets the KEY list for a SET. Any valid match for the given KEY must be set in the
                        LIST. Any SET with a KEY in its MATCHES *must* have a LIST.  List entries can be
                        newline delimited, tab delimited, or both.







