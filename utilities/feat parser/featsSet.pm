#!/usr/bin/perl 

package featsSet;

use featClass;
use strict;
use warnings;

sub new($);
sub count($);
sub add($@);
sub has($$;$);
sub all($);

#############################################################################
# new featsSet()
#
# generates a new set which holds and handles featClass only
sub new($)
{
  my($class) = shift(@_);
  my($self)  = {};

  bless $self, $class;

  $self->{list} = [];

  return $self;
}

#############################################################################
# int count()
#
# returns the number of items already in the list
sub count($)
{
  my($self) = shift(@_);
  return scalar(@{$self->{list}});
}

#############################################################################
# add(featClass [, featClass [, featClass [, ...]]])
#
# adds new featClass objects onto the current list, as long as the given
# feats do not already exist in the list
sub add($@)
{
  my($self) = shift(@_);

  foreach my $entry(@_) {
    ref($entry) eq 'featClass' or die ref($self) . ": add: expected featClass, received " . ref($entry) . "\n";
    if(!$self->has($entry->name())) {
      push @{$self->{list}}, $entry;
    }
  }

}

#############################################################################
# bool has(feat name [, ignore case])
#
# determines if any of the existing featClass object have the same name as
# the given feat name, and return true/false
sub has($$;$)
{
  my($self)     = shift(@_);
  my($featName) = shift(@_);
  my($ic)       = shift(@_) || (1==1);

  $ic and $featName = lc($featName);

  for(my $x = $self->count() - 1; $x >= 0; $x--) {
    if($ic && lc($self->{list}->[$x]->name()) eq $featName) { return (1==1); }
    if($self->{list}->[$x]->name() eq $featName) { return (1==1); }
  }

  return (1==0);
}

#############################################################################
# featClass[] getList()
#
# returns an array ref of all featClass objects in the list
sub all($)
{
  my($self) = shift(@_);
  return $self->{list};
}


1;


