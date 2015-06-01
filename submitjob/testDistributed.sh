echo Using Streams domain $STREAMS_DOMAIN_ID
echo Using Streams instance $STREAMS_DEFAULT_IID
sc -M Main
echo "Defaults:"
./submitjob.pl output/Main.sab emptyParams.txt
echo "Iliad:"
./submitjob.pl output/Main.sab iliad.txt

