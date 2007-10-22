
package Ewarn;
use strict;
use warnings;
use Carp;
use constant NO  => 0;
use constant YES => 1;

my $_file_name_previous = "";
my $_header             = "";
my $_is_first_error     = YES;
my $_is_first_line      = YES;

my %_warning_level_prefix = (
       7   => "DBG",   # DEBUG
       6   => "  -",   # INFO
       5   => "   ",   # NOTICE
       4   => "*=>",   # WARNING
       3   => "***",   # ERROR
);
sub new
{

  # Creator: Joe.Frazier
  # Create Date: Jul 27, 2007 11:53:53 PM

  my ( $proto, %args ) = @_;
  my $class = ref($proto) || $proto;
  my $self = \%args;
  bless( $self, $class );
  return $self;
}



   sub ewarn
{
       my $self = shift;
       my ( $warning_level, $message, $file_name, $line_number ) = ( @_, undef );

       # Verify if warning level should be displayed
       return if ( $self->{warning_level} < $warning_level );

       # Print the header if needed
       if ($_is_first_error)
       {
           warn $_header;
           $_is_first_error = NO;
           $_is_first_line  = NO;
       }

       # Windows and UNIX do not use the same charater in
       # the directory path. If we are on a Windows machine
       # we need to replace the / by a \.
       $file_name =~ tr{/}{\\} if $^O eq "MSWin32";

       # We make sure there is a new-line at the end of the message
       $message .= "\n" unless $message =~ /\n$/;

       # We display the file only if it is not the same are the last
       # time ewarn was called
       warn "$file_name\n" if $file_name ne $_file_name_previous;

       # We display the line number if there is one (optional parameter)
       if ( defined $line_number )
       {
           warn $_warning_level_prefix{$warning_level}
               . "(Line $line_number): $message";
       }
       else
       {
           warn $_warning_level_prefix{$warning_level}
               . "$message";
       }

       # Remember the file name for next calls
       $_file_name_previous = $file_name;
}

   ###############################################################
   # set_ewarn_filename
   # -------------------
   #
   # Replace the curent $_file_name_previous with a specific value.

sub set_ewarn_filename
{
       my $self = shift;
       $_file_name_previous = $_[0];
}

   ###############################################################
   # set_ewarn_header
   # -----------------
   #
   # Set the header for the error message and reset the
   # $_is_first_error to make the header display on the first
   # ewarn call for this header.

sub set_ewarn_header
{
       my $self = shift;
       $_is_first_error = YES;

       $_header         = $_[0];

       # There is a leas line feed unless we are at the very
       # start of the log
       $_header         = "\n" . $_header unless $_is_first_line;

       # We blank the file name to make sure it will be printed
       # with the first message after the header.
       $self->set_ewarn_filename('');
}



###
### End of the ewarn, set_ewarn_filename and
### set_ewarn_header closure.
###
###############################################################
###############################################################

