#!/usr/bin/perl
use strict;
use warnings;
use Data::Dumper;

# Henkslaaf - 2012-08-27
#
# This script sorts multi-line object LST files. Reads from stdin, writes to stdout.
#
# It handles comments well. It does not handle KEYs.
# Comments should be attached to the block they comment on:
#
# Good:
#
# # This is a sample object
# Object
# 	KEY:Object
# 	PREFEAT:1,Weapon Focus
# 	DESC:Sample Object (feat)
#
#
#
# Bad:
#
# # This is a sample object
#
# Object
# 	KEY:Object
# 	PREFEAT:1,Weapon Focus
# 	DESC:Sample Object (feat)



sub key($);

use 5.8.0;

undef $/;
my $data = <>;

my @entries = split /\n\n+/, $data;
my @sorted = sort { key($a) cmp key($b) } @entries;

print join "\n\n", @sorted;


sub key($) {
	my $entry = shift;
	my ($key) = $entry =~ /^([^#\t].*)$/m;

	return $key;
}
