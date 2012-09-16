#!/usr/bin/perl
use strict;
use warnings;
use Data::Dumper;

sub Newline() {
  $^O eq "darwin" and return "\r";
  $^O eq "MSWin32" and return "\n\r";
  return "\n";
}

use constant true     => (1==1);
use constant false    => (1==0);
use constant Tab      => "\t";
use constant OutDelim => Newline . Tab;

use featClass;
use featsSet;

sub loadFeatBasics($$);      #  filename, debug feat name
sub loadRules($);            #  rules filename
sub loadFile($;$$);          #  filename, bool (leave comments), bool (leave spaces)
sub parseDie($$$;@);         #  array ref, line number, message [, message [, ...]]
sub validateMatch($$$$);     #  lineNo, rulesFile, line, matchRule
sub parseFeat($$$$);         #  rules, featsSet class object, featClass object, featDebug
sub inList($$;$);            #  list array ref, entry to locate [, ignore case]
sub parsePrereq($$$;$);      #  rules, featsSet, featClass [, prereq line]
sub findDefault($);          #  scans the given rules list and locates the default rule, returning it

#MAIN
{
  #  check to see that they passed a feat file to parse
  my($fileToParse)    = shift(@ARGV) || undef;
  #  and possibly the name of a feat or feats to debug
  my($featDebug)      = shift(@ARGV) || '';
  my($rules, $feats);

  #  check for the help switch
  if(defined($fileToParse) and $fileToParse =~ /^(?:\-\?|\-\-help)$/) { 
    die "$0 file_to_parse [keyword feat name to match]" . Newline;
  }

  defined($fileToParse) or die "$0: no feats file given" . Newline;
  -e "$fileToParse" or die "$0: given feats file '$fileToParse' does not exist" . Newline;

  #  now load the rules for parsing
  $rules = loadRules('./featParse.rules');

  #  then load the basic data from the feats file
  $feats = loadFeatBasics($fileToParse, $featDebug);
 
  print "# Found ", $feats->count(), " feats" .
      ( $featDebug ne '' ? " (matching $featDebug)" : '' ) .
      Newline;

  #  now, loop through all feats
  foreach my $feat(@{$feats->all()}) {
    #  now we parse the feat itself and attempt to build the dataset entry
    parseFeat($rules, $feats, $feat, $featDebug);
  }

  exit;  
}

#############################################################################
# (hash reference) loadRules(rules file)
#
# parses the rules file to determine how to scan the prereq information and
# returns an hash reference of hash entries, the keys are the sets
sub loadRules($)
{
  my($rulesFile)  = shift(@_);
  my($list)       = {};
  my($set)        = '';
  my($counter)    = 0;
  my($inTweaks)   = false;
  my($inList)     = false;
  my($hasDefault) = false;
  my($from)       = '';
  my($to)         = '';
  my($in);

  $list->{TWEAKS} = [];

  open($in, $rulesFile) or die "$0: loadRules: $rulesFile: open failed: $!" . Newline;
  {
    while(my $line = <$in>) {
      $counter++;
      chomp($line);
      while($line =~ s/^\s+//g) {}
      while($line =~ s/\s+$//g) {}

      $line =~ /^#/ and next;
      $line eq '' and next;

      if($line =~ /^TWEAKS$/) {
        #  once we're looking for tweaks, nothing else matters
        $inList   = false;
        $inTweaks = true;
      }
      elsif($line =~ /^FROM\s+(.+)$/) {
        my($name) = $1;
        $inTweaks or 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK FROM found, but we're not accepting TWEAKS now");
        $to ne '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK FROM found, but TO is still set as $to");
        $from ne '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK FROM found, but FROM is still set as $from");
        $from = $name;

        $from =~ s/^"// or 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK FROM found, but FROM must start with a \"");
        $from =~ s/"$// or 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK FROM found, but FROM must end with a \"");

        while($from =~ s/\s+/\\s\+/g) {}
        $from =~ s/[\(\)\[\]]/\\$1/g;
      }
      elsif($line =~ /^TO\s+(.+)$/) {
        my($name) = $1;
        $inTweaks or 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK TO found, but we're not accepting TWEAKS now");
        $to ne '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK TO found, but TO is still set as $to");
        $from eq '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK TO found, but FROM is not set");
        $to = $name;

        $to =~ s/^"// or 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK TO found, but TO must start with a \"");
        $to =~ s/"$// or 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "TWEAK TO found, but TO must end with a \"");

        push @{$list->{TWEAKS}}, { 
          FROM => $from,
          TO   => $to
        };

        $from = '';
        $to   = '';
      }
      #  if set is not set, that's all we'll accept
      elsif($line =~ /^SET\s+([^ ]+)$/) {
        my($name) = $1;

        $inTweaks and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "SET found, but we're only accepting TWEAKS now");

        if($set ne '' && $set eq $name) { 
          parseDie([], $counter, "loadRules", $rulesFile, $line, "SET found", "but $name is a duplicate");
        }
        elsif($set eq 'TWEAKS') {
          parseDie([], $counter, "loadRules", $rulesFile, $line, "SET found", "but $name not a valid set name");
        }
        else {
          if($set ne '') {
            my($hasKey) = false;

            foreach my $match(@{$list->{$set}->{MATCHES}}) {
              if($match =~ /KEY/) {
                $hasKey = true;
                last;
              }
            }

            if($hasKey  && scalar(@{$list->{$set}->{LIST}}) < 1) {
              parseDie([], $counter, "loadRules", $rulesFile, $line, "SET '$set'", "has KEY in MATCHES, but no LIST");
            }

            $set    = '';
            $inList = false;
          }

          $set = $name;
          $list->{$set} = {};
          $list->{$set}->{LIST}         = [];      #  list of items to match against key
          $list->{$set}->{MATCHES}      = [];      #  matches to try against found items
          $list->{$set}->{OUTPUT}       = [];      #  how to output the matches key/values
          $list->{$set}->{TOKEN}        = '';      #  dataset KEY: token
          $list->{$set}->{COUNTPREMULT} = false;   #  if items are separated or grouped to one token
          $list->{$set}->{JOIN}         = false;   #  if items are separated or grouped to one token
          $list->{$set}->{KEY}          = [];      #  position of key in matches grouping (auto-set)
          $list->{$set}->{VALUE}        = [];      #  position of value in matches grouping (auto-set)
          $list->{$set}->{IGNORECASE}   = false;   #  ignore case when matching the key
          $list->{$set}->{UPPERCASE}    = false;   #  uppercase the key output
          $list->{$set}->{LOWERCASE}    = false;   #  lowercase the key output
          $list->{$set}->{UCASEFIRST}   = false;   #  uppercase first letter in each word in key output
          $list->{$set}->{KEYLEN}       = -1;      #  key output lenght limiter (0 or -1 will ignore)
          $list->{$set}->{SIGNED}       = false;   #  value gets + or -
          $list->{$set}->{DEFAULT}      = false;   #  determines which rule is used for feat names
          
        }
      }
      elsif($line =~ /^KEYLEN\s*(\d+)\s*$/) {
        my($value) = $1;

        $inTweaks and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "KEYLEN found, but we're only accepting TWEAKS now");
        $set eq '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "KEYLEN found, but no SET or LIST given");
        $inList and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "KEYLEN found, but list items or new set expected");

        $list->{$set}->{KEYLEN}       = $value < 1 ? -1 : $value;
      }
      elsif($line =~ /^MATCHES\s*(.+)\s*$/) {
        my($rule)   = $1;
        my($keyPos) = 1;
        my($valPos) = 2;

        $inTweaks and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "MATCHES found, but we're only accepting TWEAKS now");

        if($rule !~ /KEY/ && $rule !~ /VALUE/) {
          parseDie([], $counter, "loadRules", $rulesFile, $line, "MATCHES does not contain KEY or VALUE");
        }

        $set eq '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "MATCHES found, but no SET or LIST given");
        $inList and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "MATCHES found, but list items or new set expected");

        if($rule =~ /VALUE.+KEY/) {
          $valPos = 1;
          $keyPos = 2;
        }
        elsif($rule =~ /KEY.+VALUE/) {
          $keyPos = 1;
          $valPos = 2;
        }
        elsif($rule =~ /KEY/ && $rule !~ /VALUE/) {
          $keyPos = 1;
          $valPos = 0;
        }
        elsif($rule !~ /KEY/ && $rule =~ /VALUE/) {
          $keyPos = 0;
          $valPos = 1;
        }

        push @{$list->{$set}->{MATCHES}}, validateMatch($counter, $rulesFile, $line, $rule);
        push @{$list->{$set}->{KEY}}, $keyPos;
        push @{$list->{$set}->{VALUE}}, $valPos;
      }
      elsif($line =~ /^(OUTPUT|TOKEN)\s+(NOKEY)?\s*(.+)\s*$/) {
        my($type)  = $1;
        my($nokey) = ( defined($2) ? true : false );
        my($value) = $3;

        $inTweaks and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "$type found, but we're only accepting TWEAKS now");

        if($type eq 'OUTPUT') {
          if(!$nokey && $value !~ /KEY/ && $value !~ /VALUE/) {
            parseDie([], $counter, "loadRules", $rulesFile, $line, "OUTPUT does not contain KEY or VALUE");
          }
        }
        $set eq '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "$type found, but no SET or LIST given");
        $inList and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "$type found, but list items or new set expected");

        if($type eq 'OUTPUT') {
          push @{$list->{$set}->{$type}}, $value;
        }
        else {
          $list->{$set}->{$type} = $value;
        }
      }
      elsif($line =~ /^(COUNTPREMULT|JOIN|IGNORECASE|UPPERCASE|LOWERCASE|UCASEFIRST|SIGNED|DEFAULT)$/) {
        my($type) = $1;

        $inTweaks and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "$type found, but we're only accepting TWEAKS now");

        $set eq '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "$type found, but no SET or LIST given");
        $inList and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "$type found, but list items or new set expected");

        if($type eq 'DEFAULT') {
          $hasDefault and 
              parseDie([], $counter, "loadRules", $rulesFile, $line, "$type found, but another rule already tagged as default");
          $hasDefault = true;
        }

        if($type eq 'UPPERCASE') {
          if($list->{$set}->{LOWERCASE} || $list->{$set}->{UCASEFIRST}) {
            parseDie([], $counter, "loadRules", $rulesFile, $line, "UPPERCASE found, but LOWERCASE or UCASEFIRST are set");
          }
        }
        elsif($type eq 'LOWERCASE') {
          if($list->{$set}->{UPPERCASE} || $list->{$set}->{UCASEFIRST}) {
            parseDie([], $counter, "loadRules", $rulesFile, $line, "LOWERCASE found, but UPPERCASE or UCASEFIRST are set");
          }
        }
        elsif($type eq 'UCASEFIRST') {
          if($list->{$set}->{UPPERCASE} || $list->{$set}->{LOWERCASE}) {
            parseDie([], $counter, "loadRules", $rulesFile, $line, "UCASEFIRST found, but UPPERCASE or LOWERCASE are set");
          }
        }

        $list->{$set}->{$type} = true;
      }
      elsif($line =~ /^LIST\s*(.+)\s*$/) {
        my($name) = $1;

        $inTweaks and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "$name found, but we're only accepting TWEAKS now");

        #  make sure the given list name is already known
        defined($list->{$name}) or 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "found LIST $name", "but, $name is not recognized");
        #  in order to set list, matches, output, and token have to be set
        scalar(@{$list->{$set}->{MATCHES}}) < 1 and
            parseDie([], $counter, "loadRules", $rulesFile, $line, "found LIST $name", "but, MATCHES are not set");
        scalar(@{$list->{$set}->{OUTPUT}}) < 1 and
            parseDie([], $counter, "loadRules", $rulesFile, $line, "found LIST $name", "but, OUTPUT is not set");
        $list->{$set}->{TOKEN} eq '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "found LIST $name", "but, TOKEN is not set");
        $set eq '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "$name found, but no SET given");
        #  the only things we expect now are a new SET, a new LIST, or list items
        $inList = true;
        $name ne $set and $set = $name;
      }
      elsif($inList) {
        my($parts);
        $set eq '' and 
            parseDie([], $counter, "loadRules", $rulesFile, $line, "should never trigger this");

        #  in case they tab-delimited a list item on one line
        $parts = [ split(/\t+/, $line) ];

        foreach my $part(@$parts) {
          while($part =~ s/^\s+//g) {}
          while($part =~ s/\s+$//g) {}
          $part ne '' and push @{$list->{$set}->{LIST}}, $part;
        }
      }
      else {
        print "I don't know what to do with this", Newline;
        print $counter, ': ', $line, Newline;
        die Newline;
      }
    }
  }
  close($in);

  $hasDefault or 
      parseDie([], $counter, "loadRules", $rulesFile, 'End of File Reached', 'and no DEFAULT set');
  
  return $list;
}

#############################################################################
# [featsSet class] loadFeatBasics(filename, feat debug name)
#
# attempts to load the feat data from the given filename into the featsSet
# class object, which is returned - if a debug name is given, only that
# feat is analyzed, if any match (the rest of the feats are parsed for basic
# completeness, however)
sub loadFeatBasics($$)
{
  my($filename)  = shift(@_);
  my($featDebug) = shift(@_);
  my($data, $line, $lineNo, $keyOnly, $tempFeat, $feats);

  #  now load the given feats file
  $data         = loadFile($filename, true, false);

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
      }
      else {
        $tempFeat = new featClass($line);
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
      # print "benefit now $line" . Newline;
    }
    #  if at least feat, desc, and benefit are set, look for new feats
    elsif($line !~ /:/ && $tempFeat->hasDesc() && $tempFeat->hasBenefit()) {
      #  this adds the feat whether featDebug is set or empty (to match all)
      $tempFeat->name() =~ $featDebug and $feats->add($tempFeat);
      $tempFeat = undef;
      $keyOnly = false;
      #  since we've just examined the new feat name, we must rewind x and lineNo to reexamine it
      $x--;
      $lineNo--;
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
# string[] loadFile(filename [, (bool)leave extra lines [, (bool)leave spaces]])
#
# attempts to load each line of the given filename as a slice in an array
# and returns the array reference - if leave extra is true, it will leave
# all comments and empty lines in the output
sub loadFile($;$$)
{
  my($fileName)  = shift(@_);
  my($leaveGibs) = shift(@_) || false;
  my($leaveSpc)  = shift(@_) || false;
  my($list)     = [];
  my($in);

  open($in, $fileName) or die "$0: loadFile($fileName): open failed: $!" . Newline;
  {
    while(my $line = <$in>) {
      chomp($line);
      if(!$leaveSpc) {
        while($line =~ s/^\s+//g) {}
        while($line =~ s/\s+$//g) {}
      }
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
# parseDie(lines, lineNo, message [, message [, ...]])
#
# ends the program with a fancier output, as a parsing error
sub parseDie($$$;@)
{
  my($data)   = shift(@_);
  my($lineNo) = shift(@_);

  print 
      "line $lineNo", Newline,
      ( scalar(@$data) >= $lineNo ? $data->[$lineNo - 1] . Newline : '' ),
      join(Newline, @_), Newline, Newline, Newline;

  if($lineNo >= scalar(@$data)) {
    #print "Line No Exceeds line count of ", scalar(@$data), Newline . Newline;
  }
  else {
    if($lineNo > 3) {
      for(my $x = $lineNo - 3; $x <= $lineNo; $x++) { 
        print '', ( $lineNo == $x - 1 ? '**' : ''), $data->[$x], Newline;
      }
    }

    if($lineNo < scalar(@$data) - 4) {
      for(my $x = $lineNo + 1; $x <= $lineNo + 3; $x++) { 
        print $data->[$x], Newline;
      }
    }
  }

  die Newline;
}

#############################################################################
# (fixed match) validateMatch(lineNo, rulesFile, line, matchRule)
#
# attempts to validate the given match rule to make sure it can be converted
# into a regex
sub validateMatch($$$$)
{
  my($lineNo)    = shift(@_);
  my($rulesFile) = shift(@_);
  my($line)      = shift(@_);
  my($match)     = shift(@_);

  if($match !~ /KEY/ && $match !~ /VALUE/) {
    parseDie([], $lineNo, "validateMatch", $rulesFile, $line, "MATCHES does not contain KEY or VALUE");
  }

  while($match =~ s/([^\[])\[/$1(\?\:/g) {}
  while($match =~ s/^\[/(\?\:/g) {}

  while($match =~ s/\]([^\]])/)$1/g) {}
  while($match =~ s/\]$/)/g) {}

  while($match =~ s/\s+/\\s\+/g) {}

  while($match =~ s/KEY/\(.+\)/g) {}
  while($match =~ s/VALUE/\(\[\+\-\]?\\d+\)/g) {}

  eval "'' =~ /$match/";

  if(defined($@) && $@ ne '') {
    parseDie([], $lineNo, "validateMatch", $rulesFile, $line, "MATCHES regex error", $@);
  }

  return $match;
}

#############################################################################
# parseFeat(rules, featsSet, featClass, featDebug) 
#
# analyzes the given featClass object and builds out as much of the dataset
# coding as detailed in the rules then prints it
sub parseFeat($$$$)
{
  my($rules)     = shift(@_);
  my($feats)     = shift(@_);
  my($feat)      = shift(@_);
  my($featDebug) = shift(@_);
  my($output)    = '';

  #  make sure we got the right kind of objects
  ref($rules) eq 'HASH' or die "$0: parseFeat: rules: expected 'HASH', received " . ref($rules) . Newline;
  ref($feats) eq 'featsSet' or die "$0: parseFeat: feats: expected 'featsSet', received " . ref($feats) . Newline;
  ref($feat) eq 'featClass' or die "$0: parseFeat: feat: expected 'featClass', received " . ref($feat) . Newline;

  #  start the output simply
  $output = $feat->name() . OutDelim . $feat->kvType();

  #  check to parse any prerequisites if any - this is the complex part
  if($feat->hasPrereq()) {
    $output .= parsePrereq($rules, $feats, $feat);
  }

  #  then append the desc and benefit line
  $output .=
      ( $feat->hasDesc() ? OutDelim . $feat->kvDesc() : '' ) .
      ( $feat->hasBenefit() ? OutDelim . $feat->kvBenefit() : '' );
  
  #  used for testing, if the second passed param to the script is a feat name,
  #  that feat's output stops program execution
  if($featDebug ne '' &&  $feat->name() eq $featDebug) {
    print Newline . Newline . "prereq: " . $feat->prereq() . Newline . Newline;
  }

  
  $output =~ /PRETEXT/ and print "#  TODO:  fix PRETEXT entry/ies" . Newline;
  $feat->checkBenefit() and print "#  TODO:  benefit likely has items that need PRExxx prerequisites and cleaned up" . Newline;

  
  #  used for testing, if the second passed param to the script is a feat name,
  #  that feat's output stops program execution
  if($featDebug ne '') {
    if($feat->name() eq $featDebug) {
      print $output . Newline . Newline;
      die Newline;
    }
  }
  else {
    print $output, Newline, Newline;
  }
}

#############################################################################
# bool inList(list array ref, entry to locate [, ignore case])
#
# attempts to locate the given entry in the given list, case insensitive
# return true/false
sub inList($$;$)
{
  my($list) = shift(@_);
  my($find) = shift(@_);
  my($ic)   = shift(@_) or true;

  $ic and $find = lc($find);

  if(ref($list) eq 'featsSet') {
    $list->has($find, $ic) and return true;
  }
  elsif(ref($list) eq 'ARRAY') {
    for(my $x = scalar(@$list) - 1; $x >= 0; $x--) {
      if($ic && lc($list->[$x]) eq $find) { return true; }
      $list->[$x] eq $find and return true;
    }
  }
  else {
    die "$0: inList: first param should be an array ref|featsSet, received " . ref($list) . Newline;
  }

  return false;
}

#############################################################################
# string parsePrereq(featsSet, featClass, prereq line)
#
# attempts to parse the given prereq line and build out a string of dataset
# friendly output for all recognized items - returns string
sub parsePrereq($$$;$)
{
  my($rules)     = shift(@_);
  my($feats)     = shift(@_);
  my($feat)      = shift(@_);
  my($prereq)    = shift(@_) || '';
  my($delim)     = ';';
  my($featOut)   = '';
  my($prEmpty)   = false;            #  determines if output should be [] wrapped
  my($premult)   = false;            #  determines if count is used
  my($list)      = [];
  my($groups)    = {};
  my($parts);

  ref($rules) eq 'HASH' or die "$0: parseFeat: rules: expected 'HASH', received " . ref($rules) . Newline;
  ref($feats) eq 'featsSet' or die "$0: parseFeat: feats: expected 'featsSet', received " . ref($feats) . Newline;
  ref($feat) eq 'featClass' or die "$0: parseFeat: feat: expected 'featClass', received " . ref($feat) . Newline;


  #  if they didn't provide a prereq line to parse, use the full default from the feat
  if($prereq eq '') {
    $prereq  = $feat->prereq();
    $prEmpty = true;
  }

  #  then, apply any known tweaks to the prereq line
  foreach my $tweak(@{$rules->{TWEAKS}}) {
    my($from) = $tweak->{FROM};
    my($to)   = $tweak->{TO};

    while($prereq =~ s/$from/$to/ig) {
      # print "$prereq\nmatched on \"$from\"\n";
    }
  }

  #  check to see if its delimited by ; - this is unusual, but it happens
  $prereq =~ /;/ or $delim = ',';
  $parts  =  [ split(/\s*$delim\s*/, $prereq) ];

  #  if there's more than one item and the last one starts with 'or', it's a premult
  $premult = scalar(@$parts) > 1 && $parts->[scalar(@$parts) - 1] =~ s/^\s*or\s+//i;

  #  now, loop through all parts
  foreach my $part(@$parts) {
    my($partHit) = false;

    $part eq '' and next;

    #  if the item contains a , or an ' or ', it needs recursive checking
    if($part =~ /,/ || $part =~ /\s+or\s+/) {
      $part =~ s/,?\s+or\s+/, or /;
      push @$list, parsePrereq($rules, $feats, $feat, $part);
      next;
    }

    #  before we look in the rules given, let's check against feat names already
    #  loaded from the file
    if(inList($feats, $part)) {
      #  found a match!
      defined($groups->{PREFEAT}) or $groups->{PREFEAT} = { OUTPUT => [ $part ], GROUP => undef };
      #  skip to the next part
      next;
    }
    #

    #  see if the item matches any groups given from the rules - we always check
    #  (almost) every group every time, because there might be several that 
    #  produce output for any one given prereq
    foreach my $ruleGroup(keys(%$rules)) {
      #  skip our TWEAKS set
      $ruleGroup eq 'TWEAKS' and next;

      #  easy links to the group and its data
      my($group)   = $rules->{$ruleGroup};
      my($token)   = $group->{TOKEN};
      my($list)    = $group->{LIST};
      my($matches) = $group->{MATCHES};
      my($mKey)    = '';
      my($mValue)  = '';
      my($hit)     = false;

      #([+-]?\\d+) (?:th|rd|st) (?:\\s+|-) level\\s+(.+)
      if($token =~ /PRECLASS/ && $group->{OUTPUT}->[0] !~ /SPELL/ ) { # $part =~ /character/ && $token =~ /LEVEL/) {
#        print Dumper($group);
      }

      #  check to see if the groups' token exists in the groups list, used for output
      defined($groups->{$token}) or $groups->{$token} = { OUTPUT => [], GROUP => $group };

      #  loop through all match strings in the group
      for(my $x = 0; $x < scalar(@$matches); $x++) {
        my($matchRegex) = $matches->[$x];
        my($keyPos)     = $group->{KEY}->[$x];
        my($valPos)     = $group->{VALUE}->[$x];

#        if($token =~ /PRECHECKBASE/) { # && $group->{OUTPUT}->[0] !~ /SPELL/ ) {
#          print "regex: $matchRegex\n";
#          print "against: $part\n";
#          if( ($group->{IGNORECASE} && $part =~ /$matchRegex/i) || $part =~ /$matchRegex/) {
#            print "hit\n";
#          }
#        }

        #  if any one match succeeds, no other matches are tried
        if( ($group->{IGNORECASE} && $part =~ /$matchRegex/i) || $part =~ /$matchRegex/) {
       
          my($one) = $1;
          my($two) = $2 || undef;

#          if($token =~ /PRECHECKBASE/) { # && $group->{OUTPUT}->[0] !~ /SPELL/ ) {
#            print "one(" . ( defined($one) ? $one : 'undef' ) . ") two(" . ( defined($two) ? $two : 'undef' ) . ")\n";
#            print "groupkey(" . $keyPos . ") groupvalue(" . $valPos . ")\n";
#          }

          #  the way we deal with the matched data depends on the order given
          if($keyPos eq '1') { $mKey = $one; }
          elsif($keyPos eq '2' && defined($two)) { $mKey = $two; }

          if($valPos eq '1') { $mValue = $one; }
          elsif($valPos eq '2' && defined($two)) { $mValue = $two; }

#          if($token =~ /PRECHECKBASE/) { # && $group->{OUTPUT}->[0] !~ /SPELL/ ) {
#            print "mKey($mKey) mValue($mValue)\n";
#          }

          #  because we stop looking for matches when one match is met
          last; # matches
        }
      }

      #  did we get something from the regex?
      if($mKey ne '' || $mValue ne '') {
       
        #  if its a key, we need to locate the key in the provided list of matching items
        if($mKey ne '') {
          #  now, loop through our list items for this group and see if any match this key
          foreach my $listItem(@$list) {
            if($group->{IGNORECASE}) {
              if(lc($listItem) eq lc($mKey)) {
                $mKey = $listItem;
                $hit  = true;
                #  yes, we found a match in the group, so no need to look further
                last; # listItem
              }
            }
            else {
              if($listItem eq $mKey) {
                $mKey = $listItem;
                $hit  = true;
                #  yes, we found a match in the group, so no need to look further
                last;  # listitem
              }
            }
          }
        }

        #  if we don't have a hit off the key, and the key is actually empty, and
        #  we have a value, it's considered an auto hit
        if(!$hit && $mKey eq '' && $mValue ne '') { $hit = true; }
      }

      #  did we get a hit on this particular group?
      if($hit) {
        #  so we hit on the current group - update the output to replace
        #  KEY and VALUE, then push the result onto the array
        my($aOutput) = $group->{OUTPUT};

        $group->{UPPERCASE} and $mKey = uc($mKey);
        $group->{LOWERCASE} and $mKey = lc($mKey);
        $group->{UCASEFIRST} and $mKey =~ s/([\w']+)/\u\L$1/g;

        if($mValue ne '') {
          if($group->{SIGNED}) {
            if($mValue !~ /^[+-]/) {
              $mValue = '+' . $mValue;
            }
          }
          else {
            $mValue =~ s/^[+-]//;
          }
        }

        if($group->{KEYLEN} > 0) {
          $group->{KEYLEN} < length($mKey) and $mKey = substr($mKey, 0, $group->{KEYLEN});
        }

        foreach my $output(@$aOutput) {
          #  for some reason, copying the string to copyOut links to the original array
          # so I use substr to force a copy copy
          my($copyOut) = substr($output, 0); 
          while($copyOut =~ s/KEY/$mKey/g) {}
          while($copyOut =~ s/VALUE/$mValue/g) {}

          push @{$groups->{$token}->{OUTPUT}}, $copyOut;
        }

        if($token =~ /RACE/) {
          #    print Dumper($groups->{$token});
        }
        
        $partHit = true;
        # last;
      }
      
    } # end ruleGroup foreach

    $partHit and next;

    die $part . Newline . "No match\n";

  } # end part foreach - group no longer defined

  #  now, look through our gleaned info for this feat, and build the list
  foreach my $token(keys(%$groups)) {
#    print "Checking token $token in groups\n";
    my($outSet) = $groups->{$token};
    my($group)  = $outSet->{GROUP};
    my($out)    = $outSet->{OUTPUT};

    defined($group) or $group = findDefault($rules);

    if(scalar(@$out) > 0) {
      my($token)        = $group->{TOKEN};
      my($countpremult) = $group->{COUNTPREMULT};
      my($join)         = $group->{JOIN};
      my($prefix)       = $token . ':';
     
      $countpremult and $prefix .= ( $premult ? '1' : scalar(@$out) ) . ',';

      if($join) {
        push @$list, $prefix . join(',', @$out);
      }
      else {
        push @$list, $prefix . join(OutDelim . $prefix, @$out);
      }
    }
  }


  #  then see what's in the list and add it to the output
  if(scalar(@$list) > 0) {
    if($premult && scalar(@$list) > 1) {
      $featOut .= ( $prEmpty ? OutDelim : '[' ) .  "PREMULT:1,[" . join("],[", @$list) . "]" . ( $prEmpty ? '' : ']' );
     }
     else {
       $featOut .= OutDelim . join(OutDelim, @$list);
     }
   }


   # TEMP FIX
   {
     my($regex) = '(' . OutDelim . ')\[(.+)\](' . OutDelim . ')';
     $featOut =~ s/$regex/$1$2$3/m;
   }

   return $featOut;
}

#############################################################################
# (rule) findDefault(rules hash)
#
# Searches through the rules and locates the default rule, returning it
sub findDefault($)
{
  my($rules) = shift(@_);

  ref($rules) eq 'HASH' or die "$0: findDefault: expected hash reference, received " . ref($rules) . Newline;

  foreach my $ruleKey(keys(%$rules)) {
    $ruleKey eq 'TWEAKS' and next;
    $rules->{$ruleKey}->{DEFAULT} and return $rules->{$ruleKey};
  }

  die "$0: findDefault: could not locate the DEFAULT ruleset! This is serious!" . Newline;
}


__END__


























sub parsePrereq($$;$)
{
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
  my($list)        = [];





    #  some kind of class feature that is not mount
    elsif($part !~ /^mount/ && $part =~ /^(.+)\s+class\s+feature$/) {
      push @$abilityList, $1;
    }
    
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
        die "$0: parsePrereq: $part\nUnknown class/leveled item '$class'" . Newline;
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
    #  mounts
    elsif($part =~ /^(mount\sclass\sfeature|divine\sbond\s\(mount\))$/i) {
      push @$abilityList, "Special Mount";
    }
    #  we don't know what it is
    else {
      print Dumper($feat);
      die "$0: parsePrereq: $part\nUnknown prereq type" . Newline;
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
    die "$0: inList: first param should be an array ref, received " . ref($list) . Newline;
  }

  return false;
}


#############################################################################
# loadLists(hash reference)
#
# scans the current directory for 'list_xxx.txt' files, and parses their
# contents as arrays, one item per line - hash keys are xxx
sub loadLists($)
{
  my($hash) = shift(@_);
  my($dh);

  #  make sure we get a hash reference - its out only hope
  ref($hash) eq 'HASH' or die "$0: loadLists: expected hash reference, received " . ref($hash) . Newline;

  #  open the current local path and find any list_xxx.txt files
  opendir($dh, '.') or die "$0: loadLists: opendir failed: $!" . Newline;
  {
    while(my $entry = readdir($dh)) {
      #  skip hidden files
      $entry =~ /^\./ and next;
      $entry =~ /^list_([^.]+).txt$/ and $hash->{$1} = 1; # placeholder
    }
  }
  closedir($dh);

  #  now load the contents for each file
  foreach my $listName(keys(%$hash)) {
    $hash->{$listName} = loadFile("./list_$listName.txt");
  }
}

#############################################################################
# loadReplaces(hash reference)
#
# scans the current directory for 'replace_xxx.txt' files, and parses their
# contents as a groups of arrays, from/to as [0] and [1] - hash keys are xxx
sub loadReplaces($)
{
  my($hash) = shift(@_);
  my($dh);
  my($tempList);
  my($parts);
  my($count);

  #  make sure we get a hash reference - its out only hope
  ref($hash) eq 'HASH' or die "$0: loadReplaces: expected hash reference, received " . ref($hash) . Newline;

  #  open the current local path and find any list_xxx.txt files
  opendir($dh, '.') or die "$0: loadReplaces: opendir failed: $!" . Newline;
  {
    while(my $entry = readdir($dh)) {
      #  skip hidden files
      $entry =~ /^\./ and next;
      $entry =~ /^replace_([^.]+).txt$/ and $hash->{$1} = [];
    }
  }
  closedir($dh);

  $count = 0;
  #  now load the contents for each file
  foreach my $replaceName(keys(%$hash)) {
    $tempList = loadFile("./replace_$replaceName.txt", false, true);
    #  each line is a tab-delimited set of FROM and TO entries
    foreach my $tempItem(@$tempList) {
      $parts = [ split(/\t+/, $tempItem) ];
      scalar(@$parts) != 2 and 
          parseDie($tempItem, $count, "loadReplaces", $replaceName, "Expected 2 parts", "Received " . scalar(@$parts));
      push @{$hash->{$replaceName}}, $parts;
    }
    $count++;
  }

  print Dumper($hash);
  exit;
}















