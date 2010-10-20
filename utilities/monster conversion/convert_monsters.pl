# convert_monsters.pl Copyright (c) 2006 Aaron Divinsky
#!/usr/bin/perl
# version 1.1 - 2007 Chris Chandler

for ( $i = 0; $i <= $#ARGV; $i++ ) {
    $file_name = substr($ARGV[$i], rindex($ARGV[$i], "\\")+1);
    if ($file_name =~ /_mod_/) {
        next;
    }
    $base_file_name = substr($file_name, 0, rindex($file_name, "."));
    print "Processing: $file_name \n";
    open( IFH, "< $ARGV[$i]" ) || die "Can't open race input file: $!";
    $output_file = substr($ARGV[$i], 0, rindex($ARGV[$i], ".")) . "_kit.lst";
    $race_file = substr($ARGV[$i], 0, rindex($ARGV[$i], ".")) . "_new.lst";
    open( KITFH, "> $output_file" ) || die "Can't open kit out file: $!";
    print "Writing new kit file for $base_file_name\n";
    open( MONFH, "> $race_file" ) || die "Can't open race out file: $!";
    print "Writing modified race file for $base_file_name\n";
    foreach $line ( <IFH> ) {
        chomp ($line);
        if ( $line =~ /^$/ ) {
            next;
        }
        if ( $line eq "" ) {
            next;
        }
        if ( $line =~ /^#/ ) {
            next;
        }
        if ( $line =~ /^SOURCELONG:*/ ) {
            print MONFH $line . "\n";
            print KITFH $line . "\n";
            next;
        }
        @fields = split( '\t', $line );
        if ( $#fields <= 0 ) {
            next;
        }
        $name = $fields[0];
        foreach $field ( @fields ) {
            if ( $field eq $name ) {
                $kitName = $field." ~ Default";
                print KITFH "\nSTARTPACK:$kitName\tVISIBLE:QUALIFY\tEQUIPBUY:0\tPREMULT:1,[!PRERACE:1,%],[PRERACE:1,$field]\n";
                print KITFH "RACE:" . $field . "\t\t!PRERACE:1,%\n";
                print KITFH "NAME:" . $field . "\n";
                print MONFH "$name\t";
            }
            elsif ( $field =~ /PREALIGN:/ ) {
                if ( $field =~ /\Q|PREALIGN:/ ) {
                    print MONFH "$field\t";
                }
                else {
                    $field = substr($field, 9);
                    $field =~ tr/,/|/;
                    print KITFH "ALIGN:$field\n";
                }
            }

            elsif ( $field =~ /HITDICE:/ ) {
            }
            elsif ( $field =~ /^*PREDEFAULTMONSTER:Y/ ) {
                if ( $field =~ /^BONUS:CHECKS/ ) {
                }
                elsif ( $field =~ /^BONUS:COMBAT|BAB/ ) {
                }
                elsif ( $field =~ /^BONUS:SKILLRANK/ ) {
                    @vars = split( '\|', $field );
                    $skills_str = $vars[1];
                    @skills = split( ',', $skills_str );
                    foreach $skill ( @skills ) {
                        print KITFH "SKILL:" . $skill . "\tRANK:" . $vars[2] . "\n";
                    }
                }
                elsif ( $field =~ /BONUS:STAT/ ) {
                    $field = substr( $field, 0, index( $field, "|PREDEFAULTMONSTER" ) );
                    push(@bonuses, $field);
                }
            }
            elsif ( $field =~ /MFEAT:/ ) {
                $field = substr( $field, 6 );
                @feats = split( '\|', $field );
                foreach $feat ( @feats ) {
                    if ($feat =~ /,/ ) {
                        $feat_name = substr($feat, 0, rindex($feat, "("));
                        $targets_temp = substr($feat, rindex($feat, "(")+1);
                        $targets = substr($targets_temp, 0, rindex($targets_temp, ")"));
                        @FeatTargets = split( ',', $targets );
                        foreach $target ( @FeatTargets ) {
                            print KITFH "FEAT:" . $feat_name . "(" . $target .")\n";
                        }
                    }
                    else {
                        print KITFH "FEAT:" . $feat . "\n";
                    }
                }
            }
            elsif ( $field =~ /ADD:FEAT\|1\|TYPE=/ ) {
                $featType = substr( $field, 11 );
                if ( $featType =~ /ItemCreation/ ) {
                    print KITFH "FEAT:" . $featType . "\n";
                }
                elsif ( $featType =~ /Metamagic/ ) {
                    print KITFH "FEAT:" . $featType . "\n";
                }
                elsif ( $featType =~ /Drakin/ ) {
                    print MONFH "FEAT:" . $featType . "\n";
                }
                elsif ( $featType =~ /GraftMinor/ ) {
                    print MONFH "FEAT:" . $featType . "\n";
                }
                elsif ( $featType =~ /MagoriObMist/ ) {
                    print MONFH "FEAT:" . $featType . "\n";
                }
                elsif ( $featType =~ /GuildAffiliation/ ) {
                    print MONFH "FEAT:" . $featType . "\n";
                }
                elsif ( $featType =~ /HiddenShifter/ ) {
                    print MONFH "FEAT:" . $featType . "\n";
                }
                elsif ( $featType =~ /HelmedHorror/ ) {
                    print MONFH "FEAT:" . $featType . "\n";
                }
                elsif ( $featType =~ /HengeyokaiBreed/ ) {
                    print MONFH "FEAT:" . $featType . "\n";
                }
                else {
                    $outputLine = "SKILL:";
                    @skillTypes = split( ',', $featType );
                    $firstTime = 1;
                    foreach $skill ( @skillTypes ) {
                        if ( $skill =~ /Knowledge/ ) {
                            if ( $firstTime == 0 ) {
                                $outputLine .= "|";
                            }
                            $outputLine = $outputLine . "TYPE=Knowledge";
                            @ranks = split( "Knowledge", $skill );
                            if ( $ranks[0] =~ /One/ ) {
                                $count = 1;
                            } elsif ( $ranks[0] =~ /Two/ ) {
                                $count = 2;
                            } elsif ( $ranks[0] =~ /Three/ ) {
                                $count = 3;
                            } elsif ( $ranks[0] =~ /Four/ ) {
                                $count = 4;
                            } elsif ( $ranks[0] =~ /Five/ ) {
                                $count = 5;
                            } elsif ( $ranks[0] =~ /Six/ ) {
                                $count = 6;
                            }
                            $rank = substr( $ranks[1], 0, length($ranks[1]) );
                        } elsif ( $skill =~ /Craft/ ) {
                            if ( $firstTime == 0 ) {
                                $outputLine .= "|";
                            }
                            $outputLine = $outputLine . "TYPE=Craft";
                            @ranks = split( "Craft", $skill );
                            if ( $ranks[0] =~ /One/ ) {
                                $count = 1;
                            } elsif ( $ranks[0] =~ /Two/ ) {
                                $count = 2;
                            } elsif ( $ranks[0] =~ /Three/ ) {
                                $count = 3;
                            } elsif ( $ranks[0] =~ /Four/ ) {
                                $count = 4;
                            } elsif ( $ranks[0] =~ /Five/ ) {
                                $count = 5;
                            } elsif ( $ranks[0] =~ /Six/ ) {
                                $count = 6;
                            }
                            $rank = substr( $ranks[1], 0, length($ranks[1]) );
                        } elsif ( $skill =~ /Profession/ ) {
                            if ( $firstTime == 0 ) {
                                $outputLine .= "|";
                            }
                            $outputLine = $outputLine . "TYPE=Profession";
                            @ranks = split( "Profession", $skill );
                            if ( $ranks[0] =~ /One/ ) {
                                $count = 1;
                            } elsif ( $ranks[0] =~ /Two/ ) {
                                $count = 2;
                            } elsif ( $ranks[0] =~ /Three/ ) {
                                $count = 3;
                            } elsif ( $ranks[0] =~ /Four/ ) {
                                $count = 4;
                            } elsif ( $ranks[0] =~ /Five/ ) {
                                $count = 5;
                            } elsif ( $ranks[0] =~ /Six/ ) {
                                $count = 6;
                            }
                            $rank = substr( $ranks[1], 0, length($ranks[1]) );
                        } elsif ( $skill =~ /Perform/ ) {
                            if ( $firstTime == 0 ) {
                                $outputLine .= "|";
                            }
                            $outputLine = $outputLine . "TYPE=Perform";
                            @ranks = split( "Perform", $skill );
                            if ( $ranks[0] =~ /One/ ) {
                                $count = 1;
                            } elsif ( $ranks[0] =~ /Two/ ) {
                                $count = 2;
                            } elsif ( $ranks[0] =~ /Three/ ) {
                                $count = 3;
                            } elsif ( $ranks[0] =~ /Four/ ) {
                                $count = 4;
                            } elsif ( $ranks[0] =~ /Five/ ) {
                                $count = 5;
                            } elsif ( $ranks[0] =~ /Six/ ) {
                                $count = 6;
                            }
                            $rank = substr( $ranks[1], 0, length($ranks[1]) );
                        }
                        $firstTime = 0;
                    }
                    print KITFH $outputLine . "\tCOUNT:" . $count . "\tRANK:" . $rank . "\n";
                }
            }
            elsif ( $field =~ /AUTO:EQUIP/ ) {
                @equips = split( '\|', substr($field,11) );
                foreach $equip ( @equips ) {
                    print KITFH "GEAR:" . $equip . "\tSIZE:PC\tLOCATION:Equipped\n";
                }
            }
            else {
                print MONFH $field . "\t";
            }
        }
        for ( $j = 0; $j < 6; $j++ ) {
            $stat_flags[$j] = 0;
        }
        if ($#bonuses < 0 ) {
            print KITFH "STAT:STR=10|DEX=10|CON=10|INT=10|WIS=10|CHA=10\n";
        }
        while ( $#bonuses >= 0 ) {
            $bonus = shift(@bonuses);
            @splits = split('\|', $bonus);
            # throw away BONUS:STAT
            shift(@splits);
            $stats = shift(@splits);
            $value = shift(@splits);
            @stat_array = split(",", $stats);
            print KITFH "STAT:";
            $first = 1;
            foreach $stat (@stat_array) {
                if ($first ne 1) {
                    print KITFH "|";
                }
                if ( $stat eq "STR" ) {
                    $stat_flags[0] = 1;
                } elsif ( $stat eq "DEX" ) {
                    $stat_flags[1] = 1;
                } elsif ( $stat eq "CON" ) {
                    $stat_flags[2] = 1;
                } elsif ( $stat eq "INT" ) {
                    $stat_flags[3] = 1;
                } elsif ( $stat eq "WIS" ) {
                    $stat_flags[4] = 1;
                } elsif ( $stat eq "CHA" ) {
                    $stat_flags[5] = 1;
                }

                print KITFH $stat . "=" . ($value+10);
                $first = 0;
            }
            if ( $stat_flags[0] == 0 ) {
                print KITFH "|STR=10";
            } 
            if ( $stat_flags[1] == 0 ) {
                print KITFH "|DEX=10";
            } 
            if ( $stat_flags[2] == 0 ) {
                print KITFH "|CON=10";
            } 
            if ( $stat_flags[3] == 0 ) {
                print KITFH "|INT=10";
            } 
            if ( $stat_flags[4] == 0 ) {
                print KITFH "|WIS=10";
            } 
            if ( $stat_flags[5] == 0 ) {
                print KITFH "|CHA=10";
            }
            print KITFH "\n";
        }
        print MONFH "\n";
    }
    close( KITFH );
    close( MONFH );
# Comment out original RACE line in .pcc file and add new RACE and KIT lines
    $pcc_file = substr($ARGV[$i], 0, rindex($ARGV[$i], "_race")) . ".pcc";
    open ( IPCCFH, "< $pcc_file" ) || die "Can't open pcc input file: $!";
    foreach $line ( <IPCCFH> ) {
        chomp ($line);
        @PCCArray = (@PCCArray, $line);
    }
    close( IPCCFH );
    open ( OPCCFH, "+> $pcc_file" ) || die "Can't open pcc out file: $!";
    print "Modifying: ". substr($pcc_file, rindex($pcc_file, "\\")+1) . "\n\n";
    foreach $line1 (@PCCArray) {
        chomp ($line1);
        if ( $line1 =~ /^RACE:*/ ) {
            if ($line1 =~ /_mod_/) {
                print OPCCFH $line1 . "\n";
                next;
            }
            print OPCCFH "#" . $line1 . "\n";
            print OPCCFH "RACE:" . $base_file_name . "_new.lst\n";
            print OPCCFH "KIT:" . $base_file_name . "_kit.lst\n";
            next;
        } else {
            print OPCCFH $line1 . "\n";
            next;
        }
    }
    close( OPCCFH );
}
