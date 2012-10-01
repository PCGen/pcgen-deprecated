#!/usr/bin/perl
use strict;
use warnings;
use LWP::UserAgent;

$|++;

my $ua = LWP::UserAgent->new();
my $response = $ua->get("http://paizo.com/pathfinderRPG/prd/indices/spells.html");

if ($response->code() != 200) {
	warn "Could not download spell list! (" . $response->code . ")\n";
	exit 1;
}

# fetch HTML
my $html = $response->content();

# Match any hyperlink that is in a LI of class link-* (link-apg, link-core, link-um, link-uc, etc). Also match the content of the hyperlink.
# We split this into content and href later.
my @spells = $html =~ m!<li class="link-[a-z]+">(<a [^>]+>[^<]+)<!mg;
my %spells;

# Split the matched spells and put them in a hash based on the spell name as a key
for my $spell (@spells) {
	my ($link, $name) = $spell =~ m!href="([^"]+)".*?>(.*)!;

	if ($link and $name) {
		my $cleanname = lc($name);
		   $cleanname =~ s!/! !g;
		   $cleanname =~ s/[^a-z0-9 ]//g;

		$spells{$cleanname} = $link;
	}
}


my @sourcefiles = qw{data/d20ogl/paizo/pathfinder_rpg/core_rulebook/pfcr_spells.lst};


for my $sourcefile (@sourcefiles) {
	open(my $fh, "<", $sourcefile) or die "Could not open '$sourcefile': $!\n";
	my @data = <$fh>;

	for my $line (@data) {
		next if $line =~ /^#/ or $line =~ /^\s+$/;

		# Skip SOURCEXXX
		next if $line =~ /^SOURCE/;

		# Skip .MOD et al
		next if $line =~ /\.MOD/;

		# Tokenize and rewrite name to match Paizo
		my @tokens = split /\t+/, $line;
		my $name = $tokens[0];

		# rewrite Confusion (Lesser) to Confusion Lesser
		# rewrite Blindness/Deafness to Blindness Deafness
		my $cleanname = lc($name);
		   $cleanname =~ s/ \((.+)\)/, $1/g;
		   $cleanname =~ s!/! !g;
		   $cleanname =~ s/[^a-z0-9 ]//g;

		if ($spells{$cleanname}) {
			my $cleanurl = "http://paizo.com/" . $spells{$cleanname};
			   $cleanurl =~ s|#.*||g;
			   $cleanurl =~ s|(?<!http:)//+|/|g;

			my $spellresponse = $ua->head($cleanurl);

			if ($spellresponse->code != 200) {
				warn sprintf("URL for %s is invalid (%d): %s\n", $name, $spellresponse->code, $cleanurl);
				warn $spellresponse->content();
				exit;
			}
			else {
				printf("%-30.30s    %s\n", $name, $cleanurl);
			}
		}
		else {
			warn "Spell not in Paizo SRD: $cleanname or $name\n";
		}
	}
}
