#!/usr/bin/gnuplot -persist

# Impostazioni generali
set terminal postscript eps enhanced color "Arial" 16
set output 'combined_graph_english.eps'

# Impostazioni per il grafico
set title "Correlation between Network Traffic and CPU Load"
set xlabel "Time (s)"
set ylabel "Throughput (Mbit/s)"
set y2label "Cpu (%)"
set yrange [0:100]
set y2range [0:100]
set xrange [0:180]
set ytics nomirror
set y2tics
set key top left

# Plot dei dati
plot "input.txt" using 1:3 with lines title "Factory Machines Traffic","input.txt" using 1:5 with linespoints title "Client-Controller","stat.txt" using:14 title 'CPU Usage' with lines lc rgb 'green'

