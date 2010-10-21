#!/usr/bin/perl

$version = "0.1c"; #22-Aug-06

# This script reads in a PCGen LST file (or any file with spell descriptions) and converts
# all the "n/level" types of text it finds to "CASTERLEVEL*n". Please send feedback and 
# bug reports to eballot@gmail.com
#
# To use this script, you need to have perl installed (I've only tested with perl
# "v5.8.0 built for cygwin-multi-64int" so no guarantees how it works with other builds).
# "perl ParseCasterLevel.pl <raw_spells.lst >spells.lst\n";

# TODOs
# - for "*1" don't add it since it isn't necessary
# - convert ".." -> "."


# This is used to translate numbers to integers.
%atoi = (
	one => 1,
	One => 1,
	"one additional" => 1,
	"an extra" => 1,
	another => 1,
	two => 2,
	Two => 2,
	three => 3,
	Three => 3,
	four => 4,
	Four => 4,
	five => 5,
	Five => 5,
	six => 6,
	Six => 6,
	seven => 7,
	Seven => 7,
	eight => 8,
	Eight => 8,
	nine => 9,
	Nine => 9,
	ten => 10,
	Ten => 10
);


# Spelled out numbers (and words with equivalent meaning) to match. 
# NOTE: Each value in here should have a corresponding entry in %atoi
$number = "one additional|an extra|One|Two|Three|Four|Five|Six|Seven|Eight|Nine|Ten|one|two|three|four|five|six|seven|eight|nine|ten";


# This is used when matching such as "length up to 50 ft. + 5 ft./level" to define characters that
# are valid before the base number ('50' in the example). This is primarily to prevent the following
#   "caltrops in 5-ft.-by-5-ft. square, + 5-ft. square/2 levels"
# from match "5-ft.-by-5-ft. square" to "5-ft. square" (so DON'T include "-" in %validCharBeforeBase)
%validCharBeforeBase = (
	" " => 1,
	"+" => 1,
	"(" => 1,
	":" => 1
);


$postUnitText = "s| diameter| in radius| emanation| radius|\-radius emanation| of force";
$preUnitText = " quasi\-real";

# These are the various strings that could be used to indicate "per level"
$perIdentifier = "\/| per| for every";

# These are strings that sometimes follow the number before "per level" such as
# "plus one additional mount/2 levels"
$levelDescriptor = " additional| ?caster";

# Variations on "maximum"
$maxIndicator = "Max|max|max\.|maximum";


# Pass in the $afterEquation var from ParseCasterLevel. From this, creates appropriate
sub PluralizeBlurb
{
    local($subject, $postUnitText) = @_;

	# Don't pluralize if $postUnitText "s" (ie. already plural) or " radius"
	# Don't pluralize if first word is abbreviated or "feet"
	# Don't pluralize if first word is HP or HD
	# Don't pluralize if blurb ends in "dead"
	if ($postUnitText eq "s" || 
	    ($postUnitText =~ /^ radius/i) ||
		($subject =~ m/^\-/) ||
		($subject =~ m/^ ?(([a-zA-Z]*\.)|(feet))/) ||
		($subject =~ m/^ ?(HD|HP)/i) ||
		($subject =~ m/dead$/i))
	{
		$subject = $subject;
	}
	else 
	{
#print "\"$subject\" - \"$postUnitText\"\n";
		# Put the whole test in a while loop and do it 3 times so it can catch multiple
		# words that need pluarlizing. 3 is an arbitrary number.
		for (1..3)
		{
			# non-plural word preceded by 'of'
			if ($subject =~ m/^(.*?)\b(\w*)([^s ])\b( of )(.*?)$/i)
			{
				$subject = $1 . $2 . $3 . "s" . $4 . $5;
			}
			# ending in geometric shape: 'cube', 'square', 'sphere', 'globe'
			# or in word that could be adjective if located elsewhere in a phrase
			elsif ($subject =~ m/^(.*?)([^\-])\b(cube|globe|sphere|square|block|figure|giant|individual|round|target|turn)(\.\))?$/i)
			{
				$subject = $1 . $2 . $3 . "s" . $4;
			}
			# The only word is...
			elsif ($subject =~ m/^(cube|globe|sphere|square|block|day|duplicate|figure|gem|giant|hour|individual|mile|minute|missile|pebble|ray|round|serving|subject|tendril|target|turn|week)(\.\))?$/i)
			{
				$subject = $1 . "s" . $2;
			}
			# Common words: 'creature' and 'object'
			elsif ($subject =~ m/^(.*?)\b(animal|bolt|burst|chain|creature|giant|horse|humanoid|mount|object|plant|servant|tree|weapon)([^s]?)\b(.*?)$/i)
			{
				$subject = $1 . $2 . "s" . $3 . $4;
			}
			elsif ($subject =~ m/^(.*?)\b(ally)\b(.*?)$/i)
			{
				$subject = $1 . "allies" . $3;
			}
			elsif ($subject =~ m/^(.*?)\b(person)\b(.*?)$/i)
			{
				$subject = $1 . "people" . $3;
			}
			elsif ($subject =~ m/^(touch)\b(.*?)$/i)
			{
				$subject = "touches" . $3;
			}
		}
	}

	return $subject;
}


# Pass in the $afterEquation var from ParseCasterLevel. From this, creates appropriate
# CASTERLEVEL modification and returns the updated $afterEquation and caster level.
# For example, this "1d4+1 damage; +1 missile per two levels above 1st (max 5)." would
# pass in " above 1st (max 5).", which would result in
# $afterEquation = " (max 5)."
# $casterLevel = "(max(0,CASTERLEVEL-1))"
sub FixCasterLevelForAdjustment
{
    local($temp) = @_;
	my $afterEquation;
	my $casterLevel;
	if ($temp =~ m/^([^\t]*?) *(above |beyond )(\d+)(st|nd|rd|th)\b(.*)$/)
	{
		$afterEquation = $1 . $5;
		$casterLevel = "max(0,(CASTERLEVEL-" . $3 . "))";
	}
	else
	{
		$afterEquation = $temp;
		$casterLevel = "CASTERLEVEL";
	}
	return ($afterEquation, $casterLevel);
}


# Pass in the $afterEquation var from ParseCasterLevel. From this, creates appropriate
# CASTERLEVEL modification and returns the updated $afterEquation and caster level.
# For example, this "Ray that deals 1d6/2 levels (Max 10d6) damage." would pass in
#   $beforeEquation = "60-ft. cone of fire deals "
#   $equation       = "(max(1,(CASTERLEVEL/2))*1)"
#   $afterEquation  = "d6 (Max 10d6) damage."
# After parsing, these would result in
#   $equation       = "(min(10,(max(1,(CASTERLEVEL/2))*1))"
#   $afterEquation  = "d6 damage."
#
# One special case to check for:
# If the equation has a '+' in it and the maximum value is preceded by '+', then check
# the last character in $beforeEquation to see if it is a '+'. If not, then the max applies
# only to the second part of the equation. For example
#   $beforeEquation = "Cures "
#   $equation       = "(8+(CASTERLEVEL*1))"
#   $afterEquation  = "  damage (max +5) to worshiper of your patron."
# After parsing, these would result in
#   $equation       = "(8+(min(5,(CASTERLEVEL*1))))"
#   $afterEquation  = "  damage to worshiper of your patron."
#
# Otherwise, the max is applied to the entire equation like this:
#   $beforeEquation = "You gain +"
#   $equation       = "(1+((CASTERLEVEL/3)*1))"
#   $afterEquation  = " enhancement bonus to natural armor (max +5)."
# After parsing, these would result in
#   $equation       = "(min(5,(1+((CASTERLEVEL/3)*1))))"
#   $afterEquation  = " enhancement bonus to natural armor."
sub FixCasterLevelForMaximumValue
{
    local($beforeEqn, $eqn, $afterEqn, $incrVal) = @_;

	# Note the test of "$4 > $incrVal*2". This is to ensure that the number specified as the
	# max value corresponds to the value to increment per level.

	# Deal with maximum dice number such as $afterEqn = "d6 nonlethal damage (max 10d6)."
	if ($afterEqn =~ m/^d(\d+)([ \-\w]*) [\(\[]($maxIndicator) \+?(\d+)d(\d+)([^0-9\)\]]*?)[\)\]](.*)$/ &&
	    $4 > $incrVal*2)
	{
		$afterEqn = "d" . $1 . $2 . $7;
		$eqn = "(min(" . $4 . "," . $eqn . "))";
	}
	# Deal with maximum dice number such as $afterEqn = "d6 damage, max 10d6)"
	elsif ($afterEqn =~ m/^d(\d+)([ \-\w]*)\, ($maxIndicator) \+?(\d+)d(\d+)([^0-9\)\]]*?)([\)\]])(.*)$/ &&
	    $4 > $incrVal*2)
	{
		$afterEqn = "d" . $1 . $2 . $7 . $8;
		$eqn = "(min(" . $4 . "," . $eqn . "))";
	}
	# Deal with maximum dice number such as $afterEqn = " max (5d6)"
	elsif ($afterEqn =~ m/^d(\d+)([ \-\w]*) ($maxIndicator) [\(\[](\d+)d(\d+)[\)\]](.*)$/ &&
	    $4 > $incrVal*2)
	{
		$afterEqn = "d" . $1 . $2 . $6;
		$eqn = "(min(" . $4 . "," . $eqn . "))";
	}
	# Deal with dice number before maximum such as $afterEqn = "d6 nonlethal damage (10d6 max)."
	elsif ($afterEqn =~ m/^d(\d+)([ \-\w]*) [\(\[]\+?(\d+)d(\d+) (.*?)($maxIndicator)[\)\]](.*)$/ &&
	       $3 > $incrVal*2)
	{
		$afterEqn = "d" . $1 . $2 . $7;
		$eqn = "(min(" . $3 . "," . $eqn . "))";
	}
	# Deal with plain old number after maxiumum such as $afterEqn = " (max +10)."
	elsif ($afterEqn =~ m/^([ \.\-\w]*) [\(\[]($maxIndicator) (\+|\-)?(\d+)([^0-9\)\]]*?)[\)\]](.*)$/ &&
	       $4 > $incrVal*2)
	{
		$afterEqn = $1 . $6;

		# If the max number has a '+' and there is no '+' at the end of $beforeEqn
		# and $eqn contains a '+', then only apply the max to the part of the equation
		# after '+'. This is to deal with sentences like
		# "Cures 8 +1/level damage (max +5) to worshiper of your patron."
		# as opposed to
		# "You gain +1 +1/3 levels enhancement bonus to natural armor (max +5)."
		if ($3 eq "+" &&
		    substr($beforeEqn, length($beforeEqn)-1) ne "+" &&
			index($eqn, "+") > 0)
		{
			($part1, $part2) = split(/\+/, $eqn, 2);
			$eqn = $part1 . "+(min(" . $4 . "," . $part2 . "))";
		}
		else
		{
			$eqn = "(min(" . $4 . "," . $eqn . "))";
		}
	}
	# Deal with spelled out maximum number such as $afterEqn = " ray (maximum seven rays)"
	elsif ($afterEqn =~ m/^([ \-\,\w]*) [\(\[]($maxIndicator) ($number)([^0-9\)\]]*?)[\)\]](.*)$/ &&
	       $atoi{$3} > $incrVal*2)
	{
		$afterEqn = $1 . $5;
		$eqn = "(min(" . $atoi{$3} . "," . $eqn . "))";
	}
	# Deal with ", max" and plain old number followed by stop character $afterEqn = ", max 8)."
	elsif ($afterEqn =~ m/^([ \-\w]*)\, ($maxIndicator) (\+|\-)?(\d+)(\;|\.|\)|\])(.*)$/ &&
	       $4 > $incrVal*2)
	{
		$afterEqn = $5 . $6;

		# If the max number has a '+' and there is no '+' at the end of $beforeEqn
		# and $eqn contains a '+', then only apply the max to the part of the equation
		# after '+'. This is to deal with sentences like
		# "Cures 8 +1/level damage, max +5."
		# as opposed to
		# "You gain +1 +1/3 levels bonus, max +5."
		if ($3 eq "+" &&
		    substr($beforeEqn, length($beforeEqn)-1) ne "+" &&
			index($eqn, "+") > 0)
		{
			($part1, $part2) = split(/\+/, $eqn, 2);
			$eqn = $part1 . "+(min(" . $4 . "," . $part2 . "))";
		}
		else
		{
			$eqn = "(min(" . $4 . "," . $eqn . "))";
		}
	}
	# NOT HANDLED, so mark with TODO
	# "Up to (CASTERLEVEL*2) contiguous 5 ft. squares, maximum 10 squares (S)"
	# "(CASTERLEVEL*1)d6 max 10d6 vs undead."
	#  - not sure what to keep and discard after the 'maximum 10' and 'max 10d6'
	elsif ($afterEqn =~ m/^([ \-\,\.\w]*) ($maxIndicator) (\d|\d\d|$number)(.*)$/)
	{
		$eqn .= "<<TODO:add max<<";
	}
	# NOT HANDLED, so mark with TODO
	# "Ray gives 1d6 + (max(1,(CASTERLEVEL/2))*1) (max 1d6 + 5) Strength Enhancment penalty (minimum 1 Strength)."
	#  - not sure how to heuristically determine that max is '+ 5' and not '1d6'
	elsif ($afterEqn =~ m/^([ \-\,\.\w]*) [\(\[]($maxIndicator) (\d|\d\d|$number)([ \+\w]*?)[\)\]](.*)$/)
	{
		$eqn .= "<<TODO:add max<<";
	}

	return ($eqn, $afterEqn);
}


# Parsing the "per level" text requires checking for several types of sentences. These checks 
# are done in a given order starting with the most specific since a more generic check could
# match part of a more specific type. The types of checks are:
# + Base value plus per level increment (key off the '+' or 'plus' and the prepositional object '10-ft. cube' being the same)
#   "Visual figment that cannot extend beyond four 10-ft. cubes + one 10-ft. cube/level (S)"
#                                             ~~~~              ^ ~~~
# + "+n/level" - this should occure after the 'Base value plus per level increment' test
#   "Allies gain +1 on attack rolls, +1 against fear, 1d8 temporary hp +1/level (max +15)."
#                                                                      ^~
# + Dice damage - #d# followed by non-numerics and "/level"
#   "Subject faces its sins, takes 1d6 nonlethal damage/level (10d6 max)."
#                                  ~^^
# + Text with multiple numbers, the one to increment is preceded by "up to"
#   "One bridge of force 5 ft. wide, 1 in. thick, and up to 20 ft./level long"
#                                                     ^^^^^ ~~
# + Special case of two items being incremented
#   "Food and water to sustain three humans or one horse/level for 24 hours"
#                              ~~~~~        ^^ ~~~
# + Special case of "twice caster level"
#   "Multiple aberrations whose combined total Hit Dice do not exceed twice caster level in a spread emanating from the character to the extreme of the range"
#                                                                     ~~~~~~~~~~~~~~~~~~
# + Special case of "number + caster level"
#   You gain spell resistance 12 + caster level against spells with opposite alignment descriptor.
#                                  ~~~~~~~~~~~~
# + Number closest to "/level" that is not followed by a '-'
#   "1 10-ft. cube/level"
#    ~   ^
# + Finally any number closest to "/level"
#   "Spread of 10-ft. radius/level"
#              ~~
# TODO: pluralize words when necessary
# TODO: handle "max". search for (\(|\)|\*|\/|\,|\d|\d\d|\d\d\d|max|CASTERLEVEL)*
sub ParseCasterLevel
{
    local($temp, $depth) = @_;
	++$depth;

	my $beforeEquation;
	my $casterLevel;
	my $equation;
	my $afterEquation;
	# 
	# Base value plus per level increment (key off the '+' or 'plus' and the prepositional object '10-ft. cube' being the same)
	# numbers spelled out, eg. "one"
	#
	if ($temp =~ m/^(.*)\b($number)\b($preUnitText)?( |\-)?([^\t]*?)($postUnitText)?(,? ?\+ ?|,? plus )\b($number|another)\b( |\-)?([^\t]*?)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/ &&
	    $5 eq $10)
	{
		$baseVal = $atoi{$2};
		$incrVal = $atoi{$8};
		$diviser = $12;

		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$12};
		}

		$beforeEquation = $1;
		$pluralized = PluralizeBlurb($5, $6);
		$afterEquation  = $3 . $4 . $pluralized . $6 . $15;
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "(" . $baseVal . "+($casterLevel*" . $incrVal . "))";
		}
		else
		{
			$equation = "(" . $baseVal . "+(floor($casterLevel/$diviser)*" . $incrVal . "))";
		}

		# Look for "(max #)"
		($equation, $afterEquation) = FixCasterLevelForMaximumValue($beforeEquation, $equation, $afterEquation, $incrVal);

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*1$depth*";
	}
	# 
	# Base value plus per level increment (key off the '+' or 'plus' and the prepositional object '10-ft. cube' being the same)
	# numbers are numeric eg. "1"
	#
	elsif ($temp =~ m/^(.*)\b(\d+)($preUnitText)?( |\-)?([^\t]*?)($postUnitText)?(,? ?\+ ?|,? plus )\b(\d+)( |\-)?([^\t]*?)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/ &&
	       $5 eq $10 &&
		   ($1 eq "" || exists($validCharBeforeBase{substr($1, length($1)-1)}) ))
	{
		$baseVal = $2;
		$incrVal = $8;
		$diviser = $12;

		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$12};
		}

		$beforeEquation = $1;
		$afterEquation  = $3;
		if ($5 ne "")  # $4 is a space which is only needed if $5 contains text
		{
			$pluralized = PluralizeBlurb($5, $6);
			$afterEquation  = $4 . $pluralized;
		}
		$afterEquation .= $6 . $15;
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "(" . $baseVal . "+($casterLevel*" . $incrVal . "))";
		}
		else
		{
			$equation = "(" . $baseVal . "+(floor($casterLevel/$diviser)*" . $incrVal . "))";
		}

		# Look for "(max #)"
		($equation, $afterEquation) = FixCasterLevelForMaximumValue($beforeEquation, $equation, $afterEquation, $incrVal);

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*2$depth*";
	}
	#
	# "+n/level" - this should occur after the 'Base value plus per level increment' test
	#
	elsif ($temp =~ m/^(.*)(\+ ?)(\d)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/)
	{
		$incrVal = $3;
		$diviser = $5;
		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$diviser};
		}

		$beforeEquation = $1 . $2;
		$afterEquation = PluralizeBlurb($8);
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "($casterLevel*" . $incrVal . ")";
		}
		elsif ($casterLevel ne "CASTERLEVEL")
		{
			$equation = "(floor($casterLevel/$diviser)*$incrVal)";
		}
		else
		{
			$equation = "(floor(max(1,($casterLevel/$diviser)))*" . $incrVal . ")";
		}

		# Look for "(max #)"
		($equation, $afterEquation) = FixCasterLevelForMaximumValue($beforeEquation, $equation, $afterEquation, $incrVal);

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*3$depth*";
	}
	#
	# Dice damage - #d# followed by alphas and spaces and "/level"
	# Note the extra test to ensure that no spelled numbers are between the dice and the "/level"
	#
	elsif ($temp =~ m/^(.*)\b(\d)d(\d+)([ a-zA-Z\-]*?)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/ &&
	       ($foo = split($number,$4)) < 2)
	{
		$incrVal = $2;
		$diviser = $6;
		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$diviser};
		}

		$beforeEquation = $1;
		$afterEquation  = "d" . $3 . $4 . $9;
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "($casterLevel*" . $incrVal . ")";
		}
		elsif ($casterLevel ne "CASTERLEVEL")
		{
			$equation = "(floor($casterLevel/$diviser)*$incrVal)";
		}
		else
		{
			$equation = "(floor(max(1,($casterLevel/$diviser)))*" . $incrVal . ")";
		}

		# Look for "(max #d#)"
		($equation, $afterEquation) = FixCasterLevelForMaximumValue($beforeEquation, $equation, $afterEquation, $incrVal);

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*4$depth*";
	}
	#
	# Special case of two items being incremented
	#
	elsif ($temp =~ m/^(.*)\b($number)\b (\w*)( \(?or )\b($number)\b (\w*)(\))?($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/)
	{
		$incrVal1 = $2;
		if ($incrVal1 == 0)
		{
			$incrVal1 = $atoi{$incrVal1};
		}
		$incrVal2 = $5;
		if ($incrVal2 == 0)
		{
			$incrVal2 = $atoi{$incrVal2};
		}
		
		$diviser = $9;
		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$diviser};
		}

		$beforeEquation = $1;
		$pluralized = PluralizeBlurb($6, $7);
		$afterEquation  = $pluralized . $7 . $12;
		$pluralized = PluralizeBlurb($3, $4);
		my $inBetween = $pluralized . $4;
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "($casterLevel*" . $incrVal1 . ") " . $inBetween . "($casterLevel*" . $incrVal2 . ") ";
		}
		else
		{
			$equation = "(floor(max(1,($casterLevel/$diviser)))*" . $incrVal1 . ") " . $inBetween . "(floor(max(1,($casterLevel/$diviser)))*" . $incrVal2 . ") ";
		}

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*6$depth*";
	}
	#
	# Special case of "twice caster level"
	#
	elsif ($temp =~ m/^(.*) twice caster level (.*)$/)
	{
		$beforeEquation = $1;
		$afterEquation  = $2;

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . " (CASTERLEVEL*2) " . $afterEquation;
	}
	#
	# Special case of "number + caster level"
	#
	elsif ($temp =~ m/^(.*?)(\d+) \+ (your )?caster level(.*)$/)
	{
		$beforeEquation = $1;
		$afterEquation  = $4;
		$equation = "(" . $2 . "+CASTERLEVEL)";

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
	}
	#
	# Number adjacent to "/level"
	#
	elsif ($temp =~ m/^(.*)\b(\d|\d\d|\d\d\d|$number)( ?)([a-z\.]*)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/)
	{
		$incrVal = $2;
		if ($incrVal == 0)
		{
			$incrVal = $atoi{$incrVal};
		}

		$diviser = $6;
		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$diviser};
		}

		$beforeEquation = $1;
		$pluralized = PluralizeBlurb($4, $9);
		$afterEquation  = $3 . $pluralized . $9;
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "($casterLevel*" . $incrVal . ")";
		}
		elsif ($casterLevel ne "CASTERLEVEL")
		{
			$equation = "(floor($casterLevel/$diviser)*$incrVal)";
		}
		else
		{
			$equation = "(floor(max(1,($casterLevel/$diviser)))*" . $incrVal . ")";
		}

		# Look for "(max #)"
		($equation, $afterEquation) = FixCasterLevelForMaximumValue($beforeEquation, $equation, $afterEquation, $incrVal);

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*7$depth*";
	}
	#
	# Text with multiple numbers, the one to increment is preceded by "up to"
	#
	elsif (($temp =~ m/^(.*)([Uu]p to )(a? )?\b($number)\b([^\t]*?)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/) ||
		   ($temp =~ m/^(.*)([Uu]p to )(a? )?\b(\d+)([^\t]*?)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/) )
	{
		$incrVal = $4;
		if ($incrVal == 0)
		{
			$incrVal = $atoi{$incrVal};
		}
		
		$diviser = $7;
		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$diviser};
		}

		$beforeEquation = $1 . $2 . $3;
		$pluralized = PluralizeBlurb($5);
		$afterEquation  = $pluralized . $10;
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "($casterLevel*" . $incrVal . ")";
		}
		elsif ($casterLevel ne "CASTERLEVEL")
		{
			$equation = "(floor($casterLevel/$diviser)*$incrVal)";
		}
		else
		{
			$equation = "(floor(max(1,($casterLevel/$diviser)))*" . $incrVal . ")";
		}

		# Look for "(max #)"
		($equation, $afterEquation) = FixCasterLevelForMaximumValue($beforeEquation, $equation, $afterEquation, $incrVal);

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*5$depth*";
	}
	#
	# Number closest to "/level" that is not followed by a '-' or ')'
	# Note the second test looks specifically for things like 100lb since that is considered
	# a single word so the \b in the first test fails. The test for ([^\-|\d]) prevent matching
	# something like "100-ft" (which would otherwise match as '10' '0' '-ft')
	#
	elsif (($temp =~ m/^(.*)\b(\d|\d\d|\d\d\d|$number)\b([^\d\-\)\,\;])([^\t\)\;\+]*?)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/) ||
	       ($temp =~ m/^(.*)\b(\d+)([^\d\-\)\,\;])([^\t\)\;\+]*?)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/))
	{
		$incrVal = $2;
		if ($incrVal == 0)
		{
			$incrVal = $atoi{$incrVal};
		}
		
		$diviser = $6;
		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$diviser};
		}

		# This is a bit of a hack to show that the "an extra" number as an additional 
		# amount, so precede the equation with '+'.
		if ($2 eq "an extra")
		{
			$beforeEquation = $1 . "+";
		}
		else
		{
			$beforeEquation = $1;
		}
		$pluralized = PluralizeBlurb($4);
		$afterEquation  = $3 . $pluralized . $9;
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "($casterLevel*" . $incrVal . ")";
		}
		elsif ($casterLevel ne "CASTERLEVEL")
		{
			$equation = "(floor($casterLevel/$diviser)*$incrVal)";
		}
		else
		{
			$equation = "(floor(max(1,($casterLevel/$diviser)))*" . $incrVal . ")";
		}

		# Look for "(max #)"
		($equation, $afterEquation) = FixCasterLevelForMaximumValue($beforeEquation, $equation, $afterEquation, $incrVal);

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*8$depth*";
	}
	#
	# + Finally any number closest to "/level", but still don't cross ')', ',' or ';'
	#
	elsif ($temp =~ m/^(.*)\b(\d|\d\d|\d\d\d|$number)([^\t\d\)\,\;\+]*?)($perIdentifier) ?(\d?|$number)($levelDescriptor)? ?level(s?)(.*?)$/)
	{
		$incrVal = $2;
		if ($incrVal == 0)
		{
			$incrVal = $atoi{$incrVal};
		}
		
		$diviser = $5;
		# If the diviser is alpha (such as "two") then convert to numeric
		if ($diviser ne "" && $diviser == 0)
		{
			$diviser = $atoi{$diviser};
		}

		$beforeEquation = $1;
		$afterEquation  = $3 . $8;
		($afterEquation, $casterLevel) = FixCasterLevelForAdjustment($afterEquation);

		if ($diviser eq "")
		{
			$equation = "($casterLevel*" . $incrVal . ")";
		}
		elsif ($casterLevel ne "CASTERLEVEL")
		{
			$equation = "(floor($casterLevel/$diviser)*$incrVal)";
		}
		else
		{
			$equation = "(floor(max(1,($casterLevel/$diviser)))*" . $incrVal . ")";
		}

		# Look for "(max #)"
		($equation, $afterEquation) = FixCasterLevelForMaximumValue($beforeEquation, $equation, $afterEquation, $incrVal);

		# Recursively check the other parts of the string. Doing it this way prevents numerics
		# from the current equation from interfering.
		$beforeEquation = ParseCasterLevel($beforeEquation, $depth);
		$afterEquation = ParseCasterLevel($afterEquation, $depth);

		$temp = $beforeEquation . $equation . $afterEquation;
		#$temp = $temp . "*9$depth*";
	}

	return $temp;
}


print "# CASTERLEVEL fixup performed by ParseCasterLevel.pl v $version (comments to eballot@gmail.com)\n";

while ( <> )
{
	chomp;

	$result = ParseCasterLevel($_, 0);
	print $result . "\n";
}

