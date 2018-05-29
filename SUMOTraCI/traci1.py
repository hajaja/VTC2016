import os, sys

if 'SUMO_HOME' in os.environ:
     tools = os.path.join(os.environ['SUMO_HOME'], 'tools')
     sys.path.append(tools)
     print "SUMO_HOME is in the os.environ"
else:   
     print sys.path
     print os.environ
     sys.exit("please declare environment variable 'SUMO_HOME'")



