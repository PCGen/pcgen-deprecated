#!/usr/bin/perl 
# created by Brad Kester aka tripleduck 20120912
#
package featClass;

use strict;
use warnings;

sub new($$;$$$$);
sub name($;$);
sub hasName($);
sub desc($;$);
sub hasDesc($);
sub type($;$);
sub hasType($);
sub benefit($;$);
sub hasBenefit($);
sub prereq($;$);
sub hasPrereq($);
sub checkBenefit($;$);
sub kvType($);
sub kvDesc($);
sub kvBenefit($);
sub addToBenefit($$$);

#############################################################################
# new featClass(name, type [, desc [, prereq [, benefit]]])
#
# generates a new featClass object
#     name    - the name of the feat
#     type    - the type of the feat (comma or . delimited)
#     desc    - description of the feat
#     prereq  - raw prerequisite comma/semicolon-delimited list
#     benefit - benefit text
sub new($$;$$$$)
{
  my($class) = shift(@_);
  my($self)  = {};

  bless $self, $class;

  #  make sure they at least gave name 
  scalar(@_) > 0 or die ref($self) . ": new: missing parameters\n";

  #  then set up the class variables
  $self->{name}         = shift(@_);
  $self->{type}         = shift(@_) || 'General';
  $self->{desc}         = shift(@_) || '';
  $self->{prereq}       = shift(@_) || '';
  $self->{benefit}      = shift(@_) || '';
  $self->{checkBenefit} = (1==0);

  return $self;
}


#############################################################################
# [name] name(newName)
#
# gets or sets the name of the feat - if no param given, returns current 
# name - if param given, changes current name
sub name($;$) 
{
  my($self)    = shift(@_);
  my($newName) = shift(@_) || undef;

  defined($newName) or return $self->{name};

  chomp($newName);
  while($newName =~ s/^\s+//g) {}
  while($newName =~ s/\s+$//g) {}

  $self->{name} = $newName;
}

#############################################################################
# bool hasName()
#
# determines if a name is set - returns true/false
sub hasName($)
{
  my($self) = shift(@_);
  return length($self->{name}) > 0;
}

#############################################################################
# [type] type(newType)
#
# gets or sets the name of the feat - if no param given, returns current
# name - if param given, changes current name
sub type($;$)
{
  my($self)    = shift(@_);
  my($newType) = shift(@_) || undef;

  defined($newType) or return $self->{type};

  chomp($newType);
  while($newType =~ s/^s+//g) {}
  while($newType =~ s/s+$//g) {}
  while($newType =~ s/\.$//g) {}

  $self->{type} = $newType;
}

#############################################################################
# bool hasType()
#
# determines if a type is set - returns true/false
sub hasType($)
{
  my($self) = shift(@_);
  return length($self->{type}) > 0;
}

#############################################################################
# [desc] desc(newDesc)
#
# gets or sets the name of the feat - if no param given, returns current
# name - if param given, changes current name
sub desc($;$)
{
  my($self)    = shift(@_);
  my($newDesc) = shift(@_) || undef;

  defined($newDesc) or return $self->{desc};

  chomp($newDesc);
  while($newDesc =~ s/^s+//g) {}
  while($newDesc =~ s/s+$//g) {}

  $self->{desc} = $newDesc;
}

#############################################################################
# bool hasDesc()
#
# determines if a desc is set - returns true/false
sub hasDesc($)
{
  my($self) = shift(@_);
  return length($self->{desc}) > 0;
}

#############################################################################
# [prereq] prereq(newPrereq)
#
# gets or sets the name of the feat - if no param given, returns current
# name - if param given, changes current name
sub prereq($;$)
{
  my($self)      = shift(@_);
  my($newPrereq) = shift(@_) || undef;

  defined($newPrereq) or return $self->{prereq};

  chomp($newPrereq);
  while($newPrereq =~ s/^s+//g) {}
  while($newPrereq =~ s/s+$//g) {}
  while($newPrereq =~ s/\.$//g) {}

  $self->{prereq} = $newPrereq;
}

#############################################################################
# bool hasPrereq()
#
# determines if a prereq is set - returns true/false
sub hasPrereq($)
{
  my($self) = shift(@_);
  return length($self->{prereq}) > 0;
}

#############################################################################
# [benefit] benefit(newBenefit)
#
# gets or sets the name of the feat - if no param given, returns current
# name - if param given, changes current name
sub benefit($;$)
{
  my($self)       = shift(@_);
  my($newBenefit) = shift(@_) || undef;

  defined($newBenefit) or return $self->{benefit};

  chomp($newBenefit);
  while($newBenefit =~ s/^s+//g) {}
  while($newBenefit =~ s/s+$//g) {}

  $self->{benefit} = $newBenefit;
}

#############################################################################
# bool hasBenefit()
#
# determines if a benefit is set - returns true/false
sub hasBenefit($)
{
  my($self) = shift(@_);
  return length($self->{benefit}) > 0;
}

#############################################################################
# [bool] checkBenefit([bool])
#
# gets or sets whether checkBenefit is toggled
sub checkBenefit($;$)
{
  my($self) = shift(@_);
  my($bool) = shift(@_) || undef;

  defined($bool) or return $self->{checkBenefit};

  $self->{checkBenefit} = $bool;
}

#############################################################################
# string kvType()
#
# returns the KEY:value dataset entry of the type value
sub kvType($)
{
  my($self) = shift(@_);
  return "TYPE:" . $self->{type};
}

#############################################################################
# string kvDesc()
#
# returns the KEY:value dataset entry of the desc value
sub kvDesc($)
{
  my($self) = shift(@_);
  return "DESC:" . $self->{desc}; 
}

#############################################################################
# string kvBenefit()
#
# returns the KEY:value dataset entry of the benefit value
sub kvBenefit($)
{
  my($self) = shift(@_);
  return "BENEFIT:" . $self->{benefit}; 
}

#############################################################################
# addToBenefit(key, newValue)
#
# appends a newline symbol and the given [key] value data onto the end of
# benefit
sub addToBenefit($$$)
{
  my($self)  = shift(@_);
  my($key)   = shift(@_);
  my($value) = shift(@_);

  chomp($key);
  while($key =~ s/^s+//g) {}
  while($key =~ s/s+$//g) {}

  chomp($value);
  while($value =~ s/^s+//g) {}
  while($value =~ s/s+$//g) {}

  if($self->hasBenefit()) {
    $self->{benefit} .= " &nl; [$key] $value";
  }
}


1;


