#!/usr/bin/perl
################################################################################
# Copyright (C) 2015, International Business Machines Corporation
# All Rights Reserved
################################################################################

use strict;
use warnings;

use POSIX qw(strftime);
use Time::HiRes qw(gettimeofday usleep);

use Getopt::Std;
use File::Basename;

###############################################
## Constance mapping stat and statm files
###############################################
my $START_TIME=21;
my $CPU_USER=13;
my $CPU_KERNEL=14;
my $CPU_CPU=38;
my $CPU_BLOCKIO=41;
my $MEM_VIRTUAL=0;
my $MEM_REAL=1;

my $sname = basename($0);
my $help="usage: $sname -C -D -M -o <output directory> 
	-C collect CPU stats
	-D embed debug information in output file
	-M collect memory stats
	-n comma separated list of names -  use command name if not specifed
	-o output directory (default stdout)
	-p comma separated list of processes 
	-T collect thread information";

###############################################
sub usage() {

  print("$help\n");
    return;

    }

sub trim { my $s = shift; $s =~ s/^\s+|\s+$//g; return $s };

sub main() {

	my ($time, $us) = gettimeofday();
	my ($sec, $min, $hour, $day, $month, $year) = localtime $time;
	my $timestring = sprintf "%04d%02d%02d%02d%02d%02d", $year, $month, $day, $hour, $min, $sec;
	my %opts;
	getopts('CDMn:o:p:T', \%opts) or die("$help\n");
	my $outdir = "";
	my $CPU = 0;
	my $DEBUG = 0;
	my $MEMORY = 0;
	my $THREAD = 0;
	my $processes_p="";
	my $process_names_p="";

	my @cpu_cpu_in_lines;
	my @mem_cpu_in_lines;
	my $cpu_in_line;

	if ($opts{'C'}) {
		$CPU = 1;
	}
	if ($opts{'D'}) {
		$DEBUG = 1;
	}
	if ($opts{'M'}) {
		$MEMORY = 1;
	}
	if ($opts{'n'}) {
		$process_names_p = $opts{'n'};
	}
	if ($opts{'o'}) {
		$outdir = $opts{'o'};
		system("mkdir -p $outdir");
	}
	if ($opts{'p'}) {
		$processes_p = $opts{'p'};
	}
	else {
		die("$help\n");
	}
	if ($opts{'T'}) {
		$THREAD = 1;
	}

	if (!($MEMORY || $CPU)) {
		die("must collect memory or cpu stats!!!\n$help");
	}

	my $pagesizeK = `getconf PAGE_SIZE`/1024;
	my $clockTick = `getconf CLK_TCK`;
	my $host=`hostname`;
	chomp $host;

	my $header_line = "PROCESS ID, PROCESS NAME, THREAD ID";
	if ($CPU) {
		$header_line = "$header_line, UP TIME, USER CPU, KERNEL CPU, LAST SCHEDULED CPU, BLOCK IO CENTISECONDS";
	}
	if ($MEMORY) {
		$header_line = "$header_line,REAL MEMORY, VIRTUAL MEMORY";
	}
	print("$header_line\n");

	my @processes = split /,/,$processes_p;
	my @process_names = split /,/,$process_names_p;
	my $process_names_given = $#process_names;
	for (my $i = 0; $i<=$#processes; $i++) {
		my $process = $processes[$i];
		my $cpu_in_line;
		my $mem_in_line;
		if ($CPU) {
			my $cpuinfo_cmd = "cat /proc/$process/stat";
			$cpu_in_line = `$cpuinfo_cmd`;
			my $retcode = $?;
			if ($retcode) {
				die("$cpuinfo_cmd failed with $retcode\n");
			}
		}
		if ($MEMORY) {
			my $meminfo_cmd = "cat /proc/$process/statm";
			$mem_in_line = `$meminfo_cmd`;
			my $retcode = $?;
			if ($retcode) {
				die("$meminfo_cmd failed with $retcode\n");
			}
		}
		my $process_name;
		if ($i > $process_names_given) {
			if ($cpu_in_line =~ m/.+\((.+)\)/) {
				$process_name = $1;
			} 
			else {
				die("process name not found in line: $cpu_in_line\n");
			}
		}
		else {
			$process_name = $process_names[$i];
		}
		my $out_line="$process,$process_name,0";
		if ($CPU) {
			my $uptime_in_line=`cat /proc/uptime`;
			my @parse_line = split / /,$uptime_in_line;
			my $uptimeMs = $parse_line[0]*$clockTick;
			@parse_line = split / /,$cpu_in_line;
			my $startTime=$parse_line[$START_TIME];
			my $upTime=$uptimeMs-$startTime;
			my $cpu_user=$parse_line[$CPU_USER];
			my $cpu_kernel=$parse_line[$CPU_KERNEL];
			my $cpu_cpu=$parse_line[$CPU_CPU];
			my $cpu_blockio=$parse_line[$CPU_BLOCKIO];
			$out_line="$out_line,$upTime,$cpu_user,$cpu_kernel,$cpu_cpu,$cpu_blockio";
		}
		if ($MEMORY) {
			my @parse_line = split / /,$mem_in_line;
			my $mem_real = $parse_line[$MEM_REAL] * $pagesizeK;
			my $mem_virtual = $parse_line[$MEM_VIRTUAL] * $pagesizeK;
			$out_line = "$out_line,$mem_real,$mem_virtual";
		}
		print "$out_line\n";
		if ($THREAD && $CPU) {
			$cpu_in_line = `ls -m /proc/$process/task`;
			my @threads = split /,/,$cpu_in_line;
			for (my $j = 0; $j <= $#threads; $j++) {
				my $thread = trim($threads[$j]);
				my $cpuinfo_cmd = "cat /proc/$process/task/$thread/stat";
				$cpu_in_line = `$cpuinfo_cmd`;
				my $retcode = $?;
				if ($retcode) {
					die("$cpuinfo_cmd failed with $retcode\n");
				}
				my $uptime_in_line=`cat /proc/uptime`;
				my @parse_line = split / /,$uptime_in_line;
				my $uptimeMs = $parse_line[0]*$clockTick;
				@parse_line = split / /,$cpu_in_line;
				my $startTime=$parse_line[$START_TIME];
				my $upTime=$uptimeMs-$startTime;
				my $cpu_user=$parse_line[$CPU_USER];
				my $cpu_kernel=$parse_line[$CPU_KERNEL];
				my $cpu_cpu=$parse_line[$CPU_CPU];
				my $cpu_blockio=$parse_line[$CPU_BLOCKIO];
				print("$process,$process_name,$thread,$upTime,$cpu_user,$cpu_kernel,$cpu_cpu,$cpu_blockio\n");
			}
		}
	}

	($time, $us) = gettimeofday();
	my ($lasttime, $lastus) = ($time, $us);
	($sec, $min, $hour, $day, $month, $year) = localtime $time;
	$month = $month + 1;
	$year = $year + 1900;
	my $short_host = `hostname -s`;
	chomp($short_host);
	my $fbase = sprintf "stats_%s_%s", $short_host, $timestring;
	if ($DEBUG) {
		$fbase = "DEBUG_$fbase";
	}
	my $filename = "$outdir/$fbase.csv";
#	print("starting stats collection on system $host to file $filename for processes $processes_p on $interval second interval - to end do:\nrm $stopfile\n");
	return(0);
}

my $rc = main();
my ($time, $us) = gettimeofday();
printf "%s complete at %d.%06d\n",$sname,$time,$us;
exit($rc);

