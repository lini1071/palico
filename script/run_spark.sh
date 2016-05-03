#!/bin/bash

# $1 : jar file path
# $2 : number of floats in unit float-array block
# $3 : HDFS block size
# $4 : input file path
# $5 : execute count

if [ -z "$5" ]
then
	echo "Invalid parameters."
	echo "Usage: run_spark.sh <jar file> <number of floats on one block> <HDFS block size> <input file> <execute count>"
	exit $POS_PARAMS_MISSING
fi

declare path_host="hdfs://gsd-prihost:8020"

declare -i numfl
declare -i phase=0
declare -i ecount=$5
declare -i floats=$2
declare -i loopcount=$(( $3 / ($2 * 4) ))

declare -i time_start
declare -i time_end

declare path_output=""
declare merge_output=""

kill_process() {
	if [ phase == 0 ]; then kill -n 2 $$;
	elif [ phase == 1 ]; then ps "-ef" > dev/null | grep RunJar > dev/null | awk '$0!~/grep/ && $2~/[0-9]/{kill -n 2 $2}';
	elif [ phase == 2 ]; then ps "-ef" > dev/null | grep FsShell > dev/null | awk '$0!~/grep/ && $2~/[0-9]/{kill -n 2 $2}';
	fi
}
trap "kill_process; exit" SIGINT

# start of code
for (( i = 1 ; i <= ecount ; i++ ))
do
	echo "Execute count :" $i
	numfl=0

	for (( j = 0 ; j < loopcount ; j++ ))
	do
		# set number of floats more increased
		numfl+=$floats

		# execute sending jar file and wait until job ends
		#echo "Number of floats in one block :" $1

		path_output="$path_host/output/$i/$j/"
		merge_output="/home/gsd/바탕화면/output_2/res_""$i""_""$j"".img"
		
		time_start=$(date +"%s%N")
		phase=1
		spark-submit "--master" "yarn-client" $1 $numfl $4 $path_output
		wait

		# and merge each map output files to the one
		phase=2
		hadoop "fs" "-getmerge" $path_output $merge_output
		wait

		# calculate execution time
		phase=0
		time_end=$(date +"%s%N")
		#echo "Elapsed time :" $((time_end - time_start))
		echo "$i,$numfl,$(( (time_end - time_start) / 1000000 ))" >> $6

		# at the end, delete result file
		#exec "./bin/hadoop fs -rm -r" $path
		#wait
	done

	echo
done

exit 0
