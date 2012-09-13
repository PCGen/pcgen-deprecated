#!/usr/bin/perl
use strict;
use warnings;
use Data::Dumper;

use constant true => (1==1);
use constant false => (1==0);
use constant OutDelim => "\n\t";

use featClass;
use featsSet;

sub loadFile($;$);
sub inList($$);
sub parseDie($$$;@);
sub loadFeatBasics($);
sub parseFeat($$);
sub parsePrereq($$;$);

#  I abhor globals, but for this case, I'm making an exception
my($otherFeats, $classes, $races, $skills, $racialTraits, $preTexts);


my($cHit)    = shift(@ARGV);
my($counter) = 0;

if($cHit !~ /^\d+$/) {
  unshift @ARGV, $cHit;
  $cHit = -1;
}

#MAIN
{
  my($feats);
  # my($lineNo, $tempFeat, $keyOnly, $line, $feats);

  #  first, check to see if the user gave a valid filename
  my($fileToParse)    = shift(@ARGV) || undef;
 
  defined($fileToParse) or die "$0: no feats file given\n";
  -e "$fileToParse" or die "$0: given feats file '$fileToParse' does not exist\n";

  #  first, try to load the various lists
  $otherFeats   = loadFile('./otherFeats.txt');
  $classes      = loadFile('./classes.txt');
  $races        = loadFile('./races.txt');
  $skills       = loadFile('./skills.txt');
  $racialTraits = loadFile('./racialTraits.txt');
  $preTexts     = loadFile('./pretextEntries.txt');

  #  load the basic data from the feats
  $feats = loadFeatBasics($fileToParse);
 
  print "# Found ", $feats->count(), " feats\n";

  #  now, loop through all feats
  foreach my $feat(@{$feats->all()}) {
    #  now we parse the feat itself and attempt to build the dataset entry
    parseFeat($feats, $feat);
  }

  exit;
}

#############################################################################
# parseFeat(featClass) 
#
# analyzes the given featClass object and builds out as much of the dataset
# coding as I know how to do - prints it
sub parseFeat($$)
{
  my($feats)     = shift(@_);
  my($feat)      = shift(@_);
  my($output)    = '';

  #  make sure we got the right kind of objects
  ref($feats) eq 'featsSet' or die "$0: parseFeat: expected 'featsSet', received " . ref($feats) . "\n";
  ref($feat) eq 'featClass' or die "$0: parseFeat: expected 'featClass', received " . ref($feat) . "\n";

  #  start the output simply
  $output = $feat->name() . OutDelim . $feat->kvType();

  #  check to parse any prerequisites if any
  if($feat->hasPrereq()) {
    $output .= OutDelim . parsePrereq($feats, $feat);
  }

  #  then append the desc and benefit line
  $output .=
      ( $feat->hasDesc() ? OutDelim . $feat->kvDesc() : '' ) .
      ( $feat->hasBenefit() ? OutDelim . $feat->kvBenefit() : '' );
  
  #  used for testing, if the first passed param to the script is a number,
  #  that feat number's output stops program execution
  if($counter == $cHit) {
    print "\n\nprereq: " . $feat->prereq() . "\n\n";
  }

  $output =~ /PRETEXT/ and print "#  TODO:  fix PRETEXT entry/ies\n";
  $feat->checkBenefit() and print "#  TODO:  benefit likely has items that need PRExxx prerequisites and cleaned up\n";

  #  used for testing, if the first passed param to the script is a number,
  #  that feat number's output stops program execution
  if($counter == $cHit) {
    print $output . "\n\n";
    die "\n";
  }
  #  used for testing, if the first passed param to the script is a number,
  #  that feat number's output stops program execution
  $counter++;

  print $output, "\n\n";
}

#############################################################################
# string parsePrereq(prereq line)
#
# attempts to parse the given prereq line and build out a string of dataset
# friendly output for all recognized items
sub parsePrereq($$;$)
{
  my($feats)       = shift(@_);
  my($feat)        = shift(@_);
  my($prereq)      = shift(@_) || '';
  my($output)      = '';
  my($leftover)    = [];
  my($featList)    = [];
  my($abilityList) = [];
  my($statList)    = [];
  my($raceList)    = [];
  my($CLList)      = [];   #  character level
  my($SCList)      = [];   #  spell caster
  my($pvGEList)    = [];
  my($babList)     = [];
  my($skillList)   = [];
  my($visionList)  = [];
  my($classList)   = [];   #  specific classes
  my($alignList)   = [];
  my($saveList)    = [];
  my($delim)       = ';';
  my($premult)     = false;
  my($list)        = [];
  my($parts);
  my($prEmpty)     = $prereq eq '';

  ref($feat) eq 'featClass' or die "$0: parsePrereq: 2nd param: expected featClass, got " . ref($feat) . "\n";

  $prereq eq '' and $prereq = $feat->prereq();

  #  TWEAKS
  #  fixes to logic in the prereq line
  $prereq =~ s/,\s+(animal\scompanion,\sfamiliar,\sor mount class feature)\s*$/; $1/i;
  $prereq =~ s/(mountaineer)\sor\s(stability)\sracial\strait/$1 racial trait or $2 racial trait/i;
  $prereq =~ s/proficient\swith(?:\sall)?\s(martial|exotic|simple)\sweapons/$1 weapon proficiency/i;
  $prereq =~ s/(Surprise)\s(Follow)\s(Through)/$1 $2-$3/i;
  $prereq =~ s/(Adaptive)\s(Luck)/Adaptable $2/i;
  $prereq =~ s/or\s(summon\snature's\sally\sspells)$/, or ability to cast $1/i;
  $prereq =~ s/Small\ssize\sor\ssmaller/SMALL_SMALLER/i;
  $prereq =~ s/,\seither\s/, /i;
  $prereq =~ s/^either\s?(?:the)?\s//i;

# print "\n\nprereq: $prereq\n";

  #  check to see if its delimited by ; - this is unusual, but it happens
  $prereq =~ /;/ or $delim = ',';
  $parts  =  [ split(/\s*$delim\s*/, $prereq) ];

#  print "parts: ", Dumper($parts);

  #  if there's more than one item and the last one starts with 'or', it's a premult
  $premult = scalar(@$parts) > 1 && $parts->[scalar(@$parts) - 1] =~ s/^\s*or\s+//i;

  #  now, loop through all parts
  foreach my $part(@$parts) {
    $part eq '' and next;
    $part =~ s/\s*racial\s*trait//i;

    #  if the item contains a , or an ' or ', it needs recursive checking
    if($part =~ /,/ || $part =~ /\sor\s/) {
      $part =~ s/,?\s+or\s/, or /;
      push @$list, parsePrereq($feats, $feat, $part);
      next;
    }

    #  now, try to determine what the item is - is it a race?
    if(inList($races, $part)) {
      $part =~ s/([\w']+)/\u\L$1/g;
      push @$raceList, $part;
    }
    #  is it a feat?
    elsif(inList($otherFeats, $part) || inList($feats, $part)) {
      push @$featList, $part;
    }
    elsif(inList($preTexts, $part)) {
      push @$leftover, $part;
    }
    elsif(inList($racialTraits, $part)) {
      $part =~ s/([\w']+)/\u\L$1/g;
      push @$abilityList, $part;
    }

    #  is it a character level?
    elsif($part =~ /^(character\s+level)\s+(\d+)(th|rd|st)$/i) {
      push @$CLList, $2;
    }

    #  is it a stat?
    elsif($part =~ /^(con(?:stitution)?|str(?:ength)?|wis(?:dom)?|int(?:elligence)?|cha(?:risma)?|dex(?:terity)?)\s+(\d+)$/i) {
      my($stat)  = $1;
      my($value) = $2;
      push @$statList, uc(substr($stat, 0, 3)) . '=' . $value;
    }

    #  is it a channel energy something or other?
    elsif($part =~ /^(Channel\s*(?:Positive|Negative)?\sEnergy)\s(class\sfeature|\d)(?:d6)?/i) {
      my($kind) = $1 || 'Channel Energy';
      my($type) = $2;

      #  first, add the channel energy stuff to the list first fixing the capitalization
      $kind =~ s/([\w']+)/\u\L$1/g;
      push @$abilityList, $kind;

      #  then if we are a die type, recurse parse the different channeling types
      if($type =~ /^\d+$/) {
        push @$abilityList,
            parsePrereq($feats, $feat, "OracleChannelDice $type, ClericChannelPositiveEnergyDice $type, or PaladinChannelDice $type");
      }
      elsif($type =~ /^class\sfeature$/) {
        # already taken care of above
      }
      else {
        die "$part\nimplement channel $type\n";
      }
    }
    #  channel dice
    elsif($part =~ /^(OracleChannelDice|ClericChannelPositiveEnergyDice|PaladinChannelDice)\s+(\d+)$/) {
      push @$pvGEList, "$1,$2";
    }
    #  some kind of class feature that is not mount
    elsif($part !~ /^mount/ && $part =~ /^(.+)\s+class\s+feature$/) {
      push @$abilityList, $1;
    }
    #  BAB
    elsif($part =~ /^base\sattack\sbonus\s+([+-]?\d+)$/i) {
      push @$babList, $1;
    }
    #  caster level
    elsif($part =~ /^(?:caster\slevel)\s(\d+)(?:st|th|rd)$/i) {
      push @$SCList, $1;
      
    }
    #  skills
    elsif($part =~ /^([^\d]+)\s+(\d+)\s+ranks?$/) {
      my($skillName) = $1;
      my($skillRank) = $2;

      if(inList($skills, $skillName)) {
        push @$skillList, "$skillName=$skillRank";
      }
      else {
        die "$0: parsePrereq: $part\nUnknown ranked item '$skillName'\n";
      }
    }
    #  darkvision
    elsif($part =~ /^Darkvision\s+(\d+)\s+ft/i) {
      my($range) = $1;
      push @$visionList, "Darkvision=$range";
    }
    #  low-light
    elsif($part =~ /^Low-Light/i) {
      push @$visionList, "Low-Light=ANY";
    }
    #  class levels
    elsif($part =~ /^(\d+)(?:th|rd|st)[\s-]+level\s(.+)$/i || $part =~ /^(.+)\s+level\s(\d+)(?:th|st|rd)$/i) {
      my($level) = $1;
      my($class) = $2;

      if($class =~ /^\d+$/) {
        my($temp) = $class;
        $class = $level;
        $level = $temp;
      }

      if(inList($classes, $class)) {
        push @$classList, "$class=$level";
      }
      else {
        die "$0: parsePrereq: $part\nUnknown class/leveled item '$class'\n";
      }
    }
    #  non-lawful
    elsif($part =~ /^nonlawful$/i) {
      push @$alignList, "!PREALIGN:LG,LN,LE";
    }
    #  specific spell focuses
    elsif($part =~ /^Spell\sFocus\s*\(\s*([^\)]+)\s*\)$/) { 
      my($type) = $1;
      $type =~ s/([\w']+)/\u\L$1/g;
      push @$featList, "Spell Focus($type)";
    }
    #  saving throws
    elsif($part =~ /^base\s(Will|Fort|Ref)\ssave\s([+-]\d+)$/i) {
      my($type)  = $1;
      my($value) = $2;

      $type  =~ s/([\w']+)/\u\L$1/g;
      $value =~ s/^\+//g;

      push @$saveList, "$type=$value";
    }
    #  animal companion
    elsif($part =~ /^animal\scompanion$/i) {
      push @$abilityList, "Nature's Bond ~ Animal Companion", "Companion ~ Hunter's Bond";
    }
    #  familiar
    elsif($part =~ /^familiar$/i) {
      push @$abilityList, "Arcane Bond ~ Familiar";
    }
    #  mounts
    elsif($part =~ /^(mount\sclass\sfeature|divine\sbond\s\(mount\))$/i) {
      push @$abilityList, "Special Mount";
    }
    #  small or smaller size (TWEAKS)
    elsif($part =~ /^SMALL_SMALLER$/) {
      push @$list, "PRESIZELTEQ:S";
    }
    #  we don't know what it is
    else {
      print Dumper($feat);
      die "$0: parsePrereq: $part\nUnknown prereq type\n";
    }
  }

  #  now that we've sorted it all out, let's see what needs to be done
  #  PRETEXT
  if(scalar(@$leftover) > 0) {
    my($pre)  = ( scalar(@$leftover) > 1 ? '[' : '' );
    my($post) = ( scalar(@$leftover) > 1 ? ']' : '' );
    push @$list, $pre . "PRETEXT=" . join($post . ',' . $pre . 'PRETEXT=', @$leftover) . $post;
  }

  #  PREFEAT
  if(scalar(@$featList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$featList) );
    push @$list, "PREFEAT=$count," . join(',', @$featList);
  }

  #  PREABILITY
  if(scalar(@$abilityList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$abilityList) );
    push @$list, "PREABILITY=$count,CATEGORY=Special Ability," . join(',', @$abilityList);
  }

  #  PRESTAT
  if(scalar(@$statList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$statList) );
    push @$list, "PRESTAT=$count," . join(',', @$statList);
  }

  #  PRERACE
  if(scalar(@$raceList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$raceList) );
    push @$list, "PRERACE=$count," . join(',', @$raceList);
  }

  #  PRELEVEL
  if(scalar(@$CLList) > 0) {
    foreach my $prelevel(@$CLList) {
      push @$list, "PRELEVEL:MIN=$prelevel";
    }
  }

  #  PRECLASS
  if(scalar(@$SCList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$SCList) );
    push @$list, "PRECLASS=$count,SPELLCASTER=" . join(',SPELLCASTER=', @$SCList);
  }

  #  PREVARGTEQ
  if(scalar(@$pvGEList) > 0) {
    foreach my $prevargteq(@$pvGEList) {
      push @$list, "PREVARGTEQ:$prevargteq";
    }
  }

  #  PREATT
  if(scalar(@$babList) > 0) {
    foreach my $bab(@$babList) {
      $bab =~ s/^\+//g;
      push @$list, "PREATT:$bab";
    }
  } 

  #  PRESKILL
  if(scalar(@$skillList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$skillList) );
    push @$list, "PRESKILL=$count," . join(',', @$skillList);
  }

  #  PREVISION
  if(scalar(@$visionList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$visionList) );
    push @$list, "PREVISION=$count," . join(',', @$visionList);
  }

  #  PRECLASS
  if(scalar(@$classList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$classList) );
    push @$list, "PRECLASS=$count," . join(',', @$classList);
  }

  #  ALIGNMENT
  if(scalar(@$alignList) > 0) {
    foreach my $item(@$alignList) {
      push @$list, $item;
    }
  } 

  #  PRECHECKBASE
  if(scalar(@$saveList) > 0) {
    my($count) = ( $premult ? 1 : scalar(@$saveList) );
    push @$list, "PRECHECKBASE=$count," . join(',', @$saveList);
  }


  if(scalar(@$list) > 0) {
    if($premult) {
      $output .= ( $prEmpty ? '' : '[' ) .  "PREMULT:1,[" . join("],[", @$list) . "]" . ( $prEmpty ? '' : ']' );
    }
    else {
      $output .= join(OutDelim, @$list);
    }
  }

  return 
      $output;
}



#############################################################################
# [featsSet class] loadFeatBasics(filename)
#
# attempts to load the feat data from the given filename into the featsSet
# class object, which is returned
sub loadFeatBasics($)
{
  my($filename) = shift(@_);
  my($data, $line, $lineNo, $keyOnly, $tempFeat, $feats);

  #  now load the given feats file
  $data         = loadFile($filename, true);

  #  let's loop through it and parse it for basics
  $lineNo   = 0;
  $keyOnly  = false;
  $tempFeat = undef;
  $feats    = new featsSet();

  for(my $x = 0; $x < scalar(@$data); $x++) {
    $line = $data->[$x];
    $lineNo++;

    $line eq '' and next;
    $line =~ /^#/ and next;

    #  as long as the tempFeat is undefined, that's the only thing we want
    #  it cannot contain a :
    if(!defined($tempFeat)) {
      $line =~ /^(.*):/ and parseDie($data, $lineNo, "looking for feat", "found $1: instead");

      if($line =~ /\(/) {
        my($parts) = [ split(/\s*[()]\s*/, $line) ];
        scalar(@$parts) < 2 and parseDie($data, $lineNo, "trying to strip (type)", "not enough parts");
        $tempFeat = new featClass($parts->[0], $parts->[1]);
        # print Dumper($tempFeat);
      }
      else {
        $tempFeat = new featClass($line);
        # print Dumper($tempFeat);
      }
    }
    #  so, feat is set. if we're looking for a description, it should be the next thing
    elsif(!$tempFeat->hasDesc()) {
      if($tempFeat->hasPrereq() || $tempFeat->hasBenefit()) {
        parseDie($data, $lineNo, "looking for description", "however, prereq or benefit are already set");
      }

      $tempFeat->desc($line);
      #  once we have the description, we only accept key:value lines
      $keyOnly = true;
      # print "desc now $line\n";
    }
    #  found the prereq line. feat, type, and description have to be set
    elsif($line =~ s/^Prerequisites?:\s*//i) {
      $keyOnly or parseDie($data, $lineNo, "found prerequisite line", "however, we aren't expecting key: entries");
      if(!$tempFeat->hasType() || !$tempFeat->hasDesc()) {
        parseDie($data, $lineNo, "found prerequisite line", "however, type, or desc are not set");
      }
      $tempFeat->hasPrereq() and 
          parseDie($data, $lineNo, "found prerequisite line", "however, prereq is already set to:", $tempFeat->prereq());
      
      $tempFeat->prereq($line);
      # print "prereq now $line\n";
    }
    #  found the benefit line - type, prereq, and description have to be set
    elsif($line =~ s/^Benefit:\s*//i) {
      $keyOnly or parseDie($data, $lineNo, "found benefit line", "however, we aren't expecting key: entries");
      if(!$tempFeat->hasType() || !$tempFeat->hasDesc() || !$tempFeat->hasPrereq()) {
        parseDie($data, $lineNo, "found Benefit line", "however, feat, type, prereq, or desc are not set");
      }
      $tempFeat->hasBenefit() and 
          parseDie($data, $lineNo, "found benefit line", "however, benefit is already set to:", $tempFeat->benefit());
      $tempFeat->benefit($line);
      # print "benefit now $line\n";
    }
    #  if at least feat, desc, and benefit are set, look for new feats
    elsif($line !~ /:/ && $tempFeat->hasDesc() && $tempFeat->hasBenefit()) {
#      print "feat:\n", Dumper($tempFeat), "\n\n";
#      print "adding feat ", $tempFeat->name(), "\n";
      $feats->add($tempFeat);
      $tempFeat = undef;
#      print "empty feat:\n", Dumper($tempFeat), "\n\n";
      $keyOnly = false;
      #  since we've just examined the new feat name, we must rewind x and lineNo to reexamine it
      $x--;
      $lineNo--;
#      print "feat list:\n", Dumper($feats), "\n\n";
    }
    #  we can also expect some other types that are appended to the benefit
    elsif($line =~ /^\s*([^:]+)\s*:\s*(.+)\s*$/) {
      my($key)   = $1;
      my($value) = $2;
      
      $keyOnly or parseDie($data, $lineNo, "found tag '$key'", "however, we aren't expecting keys");
      if(!$tempFeat->hasType() || !$tempFeat->hasDesc() || !$tempFeat->hasPrereq() || !$tempFeat->hasBenefit()) {
        parseDie($data, $lineNo, "found tag '$key'", "however, type, prereq, benefit, or desc are not set");
      }
      
      #  because we have a unique identifier we weren't expecting
      $tempFeat->checkBenefit(true);
      $tempFeat->addToBenefit($key, $value); 
    }
    else {
      parseDie($data, $lineNo, "unexpected line");
    }
  }

  return $feats;
}



#############################################################################
# string[] loadFile(filename [, (bool)leave extra])
#
# attempts to load each line of the given filename as a slice in an array
# and returns the array reference - if leave extra is true, it will leave
# all comments and empty lines in the output
sub loadFile($;$)
{
  my($fileName)  = shift(@_);
  my($leaveGibs) = shift(@_) || false;
  my($list)     = [];
  my($in);

  open($in, $fileName) or die "$0: loadFile($fileName): open failed: $!\n";
  {
    while(my $line = <$in>) {
      chomp($line);
      while($line =~ s/^\s+//g) {}
      while($line =~ s/\s+$//g) {}
      if(!$leaveGibs) {
        $line =~ /^#/ and next;
        $line eq '' and next;
      }
      push @$list, $line;
    }
  }
  close($in);

  return $list;
}

#############################################################################
# bool inList(list array ref, entry to locate)
#
# attempts to locate the given entry in the given list, case insensitive
# return true/false
sub inList($$)
{
  my($list) = shift(@_);
  my($find) = lc(shift(@_));

  if(ref($list) eq 'featsSet') {
    $list->has($find) and return true;
  }
  elsif(ref($list) eq 'ARRAY') {
    for(my $x = scalar(@$list) - 1; $x >= 0; $x--) {
     lc($list->[$x]) eq $find and return true;
    }
  }
  else {
    die "$0: inList: first param should be an array ref, received " . ref($list) . "\n";
  }

  return false;
}

#############################################################################
# parseDie(lines, lineNo, message [, message [, ...]])
#
# ends the program with a fancier output, as a parsing error
sub parseDie($$$;@)
{
  my($data)   = shift(@_);
  my($lineNo) = shift(@_);

  print 
      "line $lineNo\n",
      $data->[$lineNo - 1], "\n",
      join("\n", @_), "\n\n\n";

  if($lineNo >= scalar(@$data)) {
    print "Line No Exceeds line count of ", scalar(@$data), "\n\n";
  }
  else {
    if($lineNo > 3) {
      for(my $x = $lineNo - 3; $x <= $lineNo; $x++) { 
        print '', ( $lineNo == $x - 1 ? '**' : ''), $data->[$x], "\n";
      }
    }

    if($lineNo < scalar(@$data) - 4) {
      for(my $x = $lineNo + 1; $x <= $lineNo + 3; $x++) { 
        print $data->[$x], "\n";
      }
    }
  }

  die "\n";
}








