print 10 % 3

from sets import Set

set1 = Set(['1', '2'])
set2 = Set(['1', '3'])

for data in set2:
    set1.add(data)

print len(set1)