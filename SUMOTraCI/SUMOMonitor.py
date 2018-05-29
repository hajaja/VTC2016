import os, sys
import optparse
import subprocess
import random

from sets import Set
from ClassLoop import ClassLoop 
import re
import csv
#cityName = "Singapore"
cityName = "Cologne"

# we need to import python modules from the $SUMO_HOME/tools directory
try:
    sys.path.append(os.path.join(os.path.dirname(__file__), '..', '..', '..', '..', "tools")) # tutorial in tests
    sys.path.append(os.path.join(os.environ.get("SUMO_HOME", os.path.join(os.path.dirname(__file__), "..", "..", "..")), "tools")) # tutorial in docs
    from sumolib import checkBinary
except ImportError:
    sys.exit("please declare environment variable 'SUMO_HOME' as the root directory of your sumo installation (it should contain folders 'bin', 'tools' and 'docs')")

import traci
# the port used for communicating with your sumo instance
PORT = 8873

NSGREEN = "GrGr" 
NSYELLOW = "yryr"
WEGREEN = "rGrG" 
WEYELLOW = "ryry"

PROGRAM = [WEYELLOW,WEYELLOW,WEYELLOW,NSGREEN,NSGREEN,NSGREEN,NSGREEN,NSGREEN,NSGREEN,NSGREEN,NSGREEN,NSYELLOW,NSYELLOW,WEGREEN]

def run():
    traci.init(PORT)
    step = 0
    intervalAggregate = 100.0 #120 seconds
    #loopNamesS = ["loop_0_15770698_0"];
    #loopNamesE = ["loop_1_190945734_0"];
    loopNamesS = readLoops("C:/Yun/workspace/SUMOJavaDP/data/" + cityName + "/loops_0.txt")
    loopNamesE = readLoops("C:/Yun/workspace/SUMOJavaDP/data/" + cityName + "/loops_1.txt")
    areaOrigin = ClassLoop("origin", loopNamesS)
    areaDestination = ClassLoop("destination", loopNamesE)
 
    while traci.simulation.getMinExpectedNumber() > 0:
        traci.simulationStep(step * 1000)
        areaOrigin.update()
        areaDestination.update()
        if step % intervalAggregate == 0:
            print "step=", step
            areaOrigin.count()    
            areaDestination.count()
        step = step + 1

        
    timeTravel = dict()
    
    for vehicleID in areaOrigin.timeEntry.keys():
        pattern = re.compile('veh_(\d+)*')
        pathNumber = pattern.match(vehicleID).group(1)
        if timeTravel.has_key(pathNumber) == False:
            timeTravel[pathNumber] = dict()
            
        if areaDestination.timeEntry.has_key(vehicleID):
            timeTravel[pathNumber][vehicleID] = areaDestination.timeEntry[vehicleID] - areaOrigin.timeEntry[vehicleID]
            print vehicleID, timeTravel[pathNumber][vehicleID]
        else:
            timeTravel[pathNumber][vehicleID] = "missing"
            print "vehicle missing at destination:", vehicleID

    csvfile = open(cityName + 'TimeTravel.csv', 'w')
    for pathNumber in timeTravel.keys():
        values = timeTravel[pathNumber].values()
        for n in range(0, len(values)):
            value = values[n]
            csvfile.write(str(value))
            if n < len(values) - 1:
                csvfile.write(',')
            else:
                csvfile.write('\n')
    csvfile.close()

def readLoops(fileAddress):
    file = open(fileAddress)
    lines = file.readlines()
    ret = []
    for line in lines:
        ret.append(line[:-1])
    return ret
    
def get_options():
    optParser = optparse.OptionParser()
    optParser.add_option("--nogui", action="store_true", default=False, help="run the commandline version of sumo")
    options, args = optParser.parse_args()
    return options

if __name__ == "__main__":
    options = get_options()

    # this script has been called from the command line. It will start sumo as a
    # server, then connect and run
    if options.nogui:
        sumoBinary = checkBinary('sumo')
    else:
        sumoBinary = checkBinary('sumo-gui')
        
    pathProj = "C:/tools/SUMO/sumo-0.22.0/" + cityName + "/"
    sumoProcess = subprocess.Popen([sumoBinary, "-c", pathProj + cityName + ".sumocfg", "--tripinfo-output", pathProj+"tripinfo.xml", "--remote-port", str(PORT)], stdout=sys.stdout, stderr=sys.stderr)
    run()
    sumoProcess.wait()