# created by Brad Kester aka tripleduck 20120912

   This utility is meant to help parse copy/pasted feats from a new source book into the dataset friendly output.
   I've only tested it with Pathfinder Advanced Race Guide on a linux system.  Likely some tweaks will have to
be made to work in Windows with ActivePerl. 

  The primary script to run is "featParser.pl fileName"

  It uses featClass.pm as the class object for a basic feat read from the given fileName.
  It uses featsSet.pm as a front-end for housing a collection of featClass objects, and being able to manage them
(a little bit).

  Included are a number of text files which contain data used for the parser to understand what items in the
prerequisite line mean. 

  classes.txt		all known classes for pathfinder
  otherFeats.txt		all known feats from all other source materials
  pretextEntries.txt		any items I didn't know how to deal with, and they get shoved into a PRETEXT
  races.txt		all known races for pathfinder
  racialTraits.txt		a list of all known racial trait names for pathfinder
  skills.txt			all known skills for pathfinder

Also included is the ARG feats text file used as the source material. It's only included to show an example of
what the layout can look like.

While I tried to break out materials as best I could for ease of updating, there are definitely some tweaking
done to prereq lines to make them more friendly for the parser.  To locate the main place that this is accomplished,
search for TWEAKS in the file.  It's down in the parsePrereq() function.

Please feel free to help update the script to perform better and more effectively!  Unfortunately, I know very little
about perl coding in Windows, so I don't really know what'll have to be done to make it friendlier.

Also, when there's an error identifying something in the feats file, it will stop and print out some basic info about
the problem.

Hope this helps!!