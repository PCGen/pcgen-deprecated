#
# get_define.pl
#-----------------
# This should go through the .lst files and 
#   find all properly (or semi-properly)
#   DEFINE: statements, and strip out the
#   name of the variable that is bonused.

# Warning: A malformed tag like "DEFINE:VariableName"
#   (without the ending '|' character) will likely cause
#   unfortunate results.

#
# intended invocation: 
#   perl get_define.pl *.lst > define.txt


use feature ':5.18';
while (<>) {
	while (/\tDEFINE:([^|]*)\|/g) { say $1; }
}
