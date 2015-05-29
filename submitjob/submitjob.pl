#!/usr/bin/perl

use strict;

sub buildCommandLine($$) {
my ($configFile,$isStandalone) = @_;

my $toReturn = "";
open(INFILE,"<$configFile");

while(<INFILE>) {
  next if /^\#/;
  next if /^\s*$/;
  if (/\s*([^= ]+)\s*=\s*(.+)\s*$/) {
     my $left = $1;
     my $right = $2;
     chomp($right);
     if ($isStandalone) {
       $toReturn .= " $left=\"$right\"";
     }
     else {
    if ($toReturn ne "") {
       $toReturn .= "\\\n\t";
        }
    $toReturn .= " -P $left=\"$right\"";
     }
  }
  else {
    print STDERR "Malformed line: $_";
  }
}
return $toReturn;
}

sub main {

if ($ARGV[0] eq "-h" || $ARGV[0] eq "--help" || $ARGV[0] eq "-?") {
   print "Usage: submitjob.pl <sabfile> <parameterfile> [extra streamtool arguments]\n";
   exit 0;
}

if (scalar(@ARGV) < 2) {
    print "Usage: submitjob.pl <sabfile> <parameterfile> [extra sreamtool arguments]\n";
}

my $app = $ARGV[0];
my $configFile = $ARGV[1];
my @theRest = splice(@ARGV,2);
my $standalone = 0;
if ($ARGV[0] =~ /standalone/) {
    $standalone = 1;
}
my $args = buildCommandLine($configFile,$standalone);

my $remainingArgs = join(" ",@theRest);
my $cmd = "streamtool submitjob $app $args $remainingArgs";
if ($standalone) {
    $cmd = "$app $args $remainingArgs";
}
#print $cmd;
#print "\n";
system($cmd);
}
main();

