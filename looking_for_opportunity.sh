#!/bin/bash
symbol=$1
echo "Degree="$2
echo "Lookback="$3

iteration=0
while true; do
	current_price=$(./getPrice.sh $symbol)
	echo $iteration	$current_price
	echo $current_price >> $symbol"_data.csv"
	((iteration++))
	sleep 1
	java -jar CurveFitSingleColumnDynamicFit.jar <(cat $symbol"_data.csv") $2 $3
done
