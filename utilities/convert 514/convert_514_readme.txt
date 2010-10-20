convert_514.pl  v.85
Copyright (c) 2008 Chris 'Barak' Chandler

FUNCTION:  
========
Update PCGen data files (.lst) from version 5.12 to 5.14

INSTALLATION
============
A) Get Perl if you don't already have it
      I'm using ActivePerl v5.10 (build 1002) but any standard distribution with version 5.8 and over should work

B) Put the script somewhere
     Once Perl is installed on your computer, you just have to find a home for the script. 

C) Run the script
     All you have to do is type "perl convert_514.pl" with the proper parameters to make it work.

COMMAND LINE PARAMETERS:
========================
-S --> Create generic shieldprof file (Optional)

-I="<directory>" --> input directory (Required)
	directory name must be in double quotes if it contains spaces

-O="<directory>" --> output directory (Required)
	directory name must be in double quotes if it contains spaces

NOTES:
======

1) If you specify the same output directory as input directory your files will be overwritten, so make sure to backup your files in another location if you choose to do this. NOTE: This is *NOT* the recommended method

2) The script will remove CHOICE tags that do not have targets that are schools, subschools or descriptors.  It will log these occurrences.

3) Any CHOOSE tag with only one choice (like the old "CHOOSE:+3 HP" for the Toughness feat) will be converted to CHOOSE:NOCHOICE

4) Any CHOOSE tag that does not have a first parameter that matches one of the documented ones will be converted to CHOOSE:STRING (in a non-equipmod file) or CHOOSE:STRING|...|TITLE=yyy in an equipmod file (the title will be the text between the colon and the first pipe)

5) Any tags that need to be manually updated will be passed through as is and noted in the log file (line numbers given reference *input files*)

6) Recommended usage is in two steps:
	A) Point the script at your existing gamemode directory and direct output to the 5.14 gamemode directory
	B) Point the script at your existing data directory and direct output to the 5.14 data directory



TAG CONVERSIONS/ADDITIONS:
==========================
SA -> SAB  (Logs those that need manual conversion - basically SA:.CLEAR)

PROFICIENCY -> PROFICIENCY:<subtoken>|<prof>

AUTO:ARMORPROF|TYPE -> AUTO:ARMORPROF|ARMORTYPE=

AUTO:SHIELDPROF|TYPE ->  AUTO:SHIELDPROF|SHIELDTYPE=

FEATAUTO -> AUTO:FEAT 

REPEATLEVEL -> <level #>:REPEATLEVEL

PREDEITY -> PREDEITY:x,y,y

PRETEMPLATE -> PRETEMPLATE:x,y,y

PRERACE:y -> PRERACE:x,y,y

PRELEVEL: -> PRELEVEL:MIN=#

PRELEVELMAX -> PRELEVEL:MAX=#

CONTAINS:-1 -> CONTAINS:UNLIM

MOVECLONE:w,x,y,z -> MOVECLONE:x,y,z (Logs those that need manual conversion)

PRETYPE: -> PRETYPE:x,y,y

ADD:SA -> ADD:SAB

KNOWNSPELLS:.CLEAR<stuff> -> KNOWNSPELLS:.CLEAR|<stuff>

PREHD:#-# -> PREHD:MIN=x,MAX=y

CHOICE -> CHOICE:SCHOOL 
     (Abjuration, Conjuration, Divination, Enchantment, Evocation, Illusion, 
      Necromancy, Transmutation, Universal)

CHOICE -> CHOICE:SUBSCHOOL 
     (Creation, Compulsion, Scrying, Glamer, Charm, Pattern, Summoning, Healing, Teleportation, 
      Phantasm, Calling, Figment, Shadow)

CHOICE -> CHOICE:DESCRIPTOR
     (Acid, Mind-Affecting, Air, Sonic, Evil, Fear, Force, Good, Fire, Electricity, Chaotic, 
      Cold, Death, Language-Dependent, Light, Water, Darkness, Death, Lawful)

FAVCLASS -> FAVCLASS:<class>.<subclass> 
     (Wizard, Abjurer, Conjurer, Diviner, Enchanter, Evoker, Illusionist,    
      Necronmancer, Transmuter, Seer, Shaper, Kineticist, Egoist, Nomad, Telepath)

FAVOREDCLASS -> FAVOREDCLASS:<class>.<subclass>	
     (Wizard, Abjurer, Conjurer, Diviner, Enchanter, Evoker, Illusionist, 
      Necronmancer, Transmuter, Seer, Shaper, Kineticist, Egoist, Nomad, Telepath)

Create Armorprof file from armor.lst files

PRECLASS -> PRECLASS:#,y,y conversion

PREMOVE -> PREMOVE:#,y,y conversion

PREWIELD -> PREWIELD:#,y,y conversion

PREWEAPONPROF -> PREWEAPONPROF:#,y,y conversion

PRESPELLSCHOOL:Abjuration,1,5 -> PRESPELLSCHOOL:1,Abjuration=5

PRESPELLSCHOOLSUB:Healing,1,5 -> PRESPELLSCHOOLSUB:1,Healing=5

PRESPELLDESCRIPTOR:Evil,1,5 -> PRESPELLDESCRIPTOR:1,Evil=5

CHOOSE:Language(<type>) -> CHOOSE:LANGUAGE|<type>

CHOOSE:SKILLSNAMED|...|# ->  CHOOSE:SKILLSNAMED|...<tab> SELECT:#

SPELLS:<spellbook>|TIMES:-1 to SPELLS:<spellbook>|TIMES:ATWILL

NUMCHOICES=1 not allowed in CHOOSE:SPELLLEVEL

CHOOSE: in EqMod with Title as first argument is deprecated -> CHOOSE:<subtoken>|<args>|TITLE=<title>

REMOVE:FEAT(<feat1>,<feat2>)# -> REMOVE:FEAT|<feat1>,<feat2>|#

CHOOSE:<blah text1>|<blah text2> -> CHOOSE:STRING|<blah text1>|<blah text2>

CHOOSE:CCSKILLLIST|x,x -> CHOOSE:SKILLSNAMED|CROSSCLASS

CHOOSE:NONCLASSSKILLLIST|x -> CHOOSE:SKILLSNAMED|CROSSCLASS|EXCLUSIVE

CHOOSE:SKILLIST|x,x -> CHOOSE:SKILLSNAMED|x|x

CHOOSE:SKILLIST|LIST -> CHOOSE:SKILLSNAMED|ALL

CHOOSE:Martial -> CHOOSE:PROFICIENCY|WEAPON|UNIQUE|TYPE.Martial

CHOOSE:Exotic -> CHOOSE:PROFICIENCY|WEAPON|UNIQUE|TYPE.Exotic

CHOOSE:SPELLLEVEL arguments may not contain ,

LIST in some CHOOSEs no longer used

ADD:FEAT(blah,blah1)# -> ADD:FEAT|#|blah,blah1

ADD:CLASSSKILLS(blah,blah1)# -> ADD:CLASSSKILLS|#|blah,blah

ADD:SPELLCASTER(blah,blah1)# -> ADD:SPELLCASTER|#|blah,blah 

PRESPELLTYPE:Arcane,1,5 -> PRESPELLTYPE:1,Arcane=5

RESIZABLEEQUIPTYPE -> Add default to miscinfo.lst

WEAPONREACH -> Add default to miscinfo.lst

FOLLOWERALIGN -> PREALIGN 

PREVIEWDIR:d20/fantasy -> added to miscinfo.lst as a commented out example

PREVIEWSHEET:preview.html -> added to miscinfo.lst as a commented out example

VISION:Low-light,Darkvision -> VISION:Low-light|Darkvision

CLASSES:Sorcerer=1[PRExxx] -> CLASSES:Sorcerer=1<tab>PRExxx

PRESA:Wild Empathy -> PRESAB:Wild Empathy

WT:- -> <tab>

ADD:Language -> ADD:LANGUAGE

BONUSFEATS:1 -> BONUS:FEAT|POOL|1

PREEQUIP:<equipment> -> PREEQUIP:#,<equipment>

HISTORY:
========

v.85
-- Fixed bug where REPEATLEVEL already in new format was being deleted from the line
-- Fixed bug where ADD:FEAT(Blah) became ADD:FEAT||Blah because there was no trailing number
-- Added PREEQUIP conversion

v.8
-- Added PRESPELLDESCRIPTOR conversion
-- Added ADD:Language conversion
-- Added BONUSFEATS conversion
-- Added fix for common VISION tag error (using commas instead of pipes)
-- Added fix for CLASSES tag with embedded PRExxxs attached
-- Added removal of invalid "WT:-" entries in equipment files
-- Fixed script so it will only process lst or pcc files
-- Fixed bug with SA inside of a HD tag not converting to SAB
-- Fixed bug with SA inside of a LEVEL tag not converting to SAB
-- Fixed bug where script added WEAPON to a tag that already had PROFICIENCY:WEAPON
-- Fixed bug where CHOOSE:NUMCHOICES not being processed for a STRING subtoken
-- Fixed bug where CHOOSE:FEATSELECT was being processed into a CHOOSE:STRING
-- Fixed bug with CHOOSE:NONCLASSSKILLLIST not converting fully
-- Fixed bug with CHOOSE:CCSKILLLIST not converting fully
-- Fixed bug with PRESA being converted to PRESAB

v .7
-- Fixed bug with shield proficiency processing (TYPE tag processed twice)
-- Fixed bug with shield proficiency error message being generated in log file
-- Fixed bug with TYPE tag for shield proficiencies not being tab separated from the next tag occasionally
-- Fixed bug with Barding proficiencies not being created in basic armorprof files

v.65
-- Fixed bug with workarray and CHOOSE tags in equipment modifiers
-- Fixed bug with PREMULTs with PRESPELLTYPE having brackets stripped
-- Fixed bug with CHOOSE processing & choices containing restricted characters
-- Added RESIZABLEEQUIPTYPE processing
-- Added WEAPONREACH processing
-- Added FOLLOWERALIGN conversion
-- Added PREVIEWDIR to miscinfo (commented out so it may serve as an example)
-- Added PREVIEWSHEET to miscinfo (commented out so it may serve as an example)

v.6
-- Added PRESPELLSCHOOLSUB conversion
-- Added ADD:FEAT conversion
-- Added ADD:CLASSSKILLS conversion
-- Added ADD:SPELLCASTER conversion
-- Added PRESPELLTYPE conversion

v.55
-- Changed script behavior to pass through blank lines rather than remove them
-- Added a catch-all to the Shield proficiency section to cover shields that don't have the proper type needed to deteemine proficiency
-- Changed script behavior to pass through already existing armor proficiency tags
-- Fixed bug with file names on Mac/Unix systems
-- Fixed bug with "I" contained in the input path
-- Changed command line options to use "=" sign
-- Fixed bug with AUTO:ARMORPROF|TYPE and AUTO:SHIELDPROF|TYPE conversions
-- Fixed bug with PRERACE:% not being converted to PRERACE:1,%
-- Fixed extra tab when SAB tag was last tag on line
-- Added PREWEAPONPROF conversion
-- Fixed missing tab when there were multiple FEATAUTO tags on the same line

v.5 -- Rough Beta release
