sc -T -M Main
echo "Defaults:"
./submitjob.pl output/bin/standalone emptyParams.txt
echo "Iliad:"
./submitjob.pl output/bin/standalone iliad.txt

