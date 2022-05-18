# hamspam.py

import sys

input = sys.argv[1]
ham = sys.argv[2]
spam = sys.argv[3]

input_f = open(input, 'r')
ham_f = open(ham, 'w')
spam_f = open(spam, 'w')

for line in input_f:
	line_n = line.split('\t', 1)
	if line_n[0] == "ham":
		ham_f.write(line_n[1])
	elif line_n[0] == "spam":
		spam_f.write(line_n[1])
	else:
		print(line_n[0])
		
input_f.close()
ham_f.close()
spam_f.close()
