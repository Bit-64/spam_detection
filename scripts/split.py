# split.py

import sys
import random

def partition(l, pred):
    yes, no = [], []
    for e in l:
        if pred(e):
            yes.append(e)
        else:
            no.append(e)
    return yes, no

ham = open(sys.argv[1], 'r').readlines()
spam = open(sys.argv[2], 'r').readlines()
lines1, lines2 = partition(ham, lambda x: random.random() < 0.5)
lines3, lines4 = partition(spam, lambda x: random.random() < 0.5)

ham_train = open(sys.argv[3], 'w')
ham_test = open(sys.argv[4], 'w')
spam_train = open(sys.argv[5], 'w')
spam_test = open(sys.argv[6], 'w')

for line in lines1:
	ham_train.write(line)
for line in lines2:
	ham_test.write(line)
for line in lines3:
	spam_train.write(line)
for line in lines4:
	spam_test.write(line)

