#
# get_bonus_var.pl
#-----------------
# This should go through the .lst files and 
#   find all properly (or semi-properly)
#   BONUS:VAR| statements, and strip out the
#   name of the variable that is bonused.

# Warning: A tag like "BONUS:VAR|VariableOne,Variable2|17"
#   will likely need some post-editing.
#   It doesn't strip out each variable separately (yet?).

# Warning: A malformed tag like "BONUS:VAR|VariableName"
#   (without the ending '|' character) will likely cause
#   unfortunate results.

#
# intended invocation: 
#   perl get_bonus_var.pl *.lst > bonus_var.txt


use feature ':5.18';
while (<>) {
	while (/\tBONUS:VAR\|([^|]*)\|/g) { say $1; }
}
