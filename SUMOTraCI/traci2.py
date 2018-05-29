#!/usr/bin/env python
"""
@file    runner.py
@author  Lena Kalleske
@author  Daniel Krajzewicz
@author  Michael Behrisch
@author  Jakob Erdmann
@date    2009-03-26
@version $Id: runner.py 17235 2014-11-03 10:53:02Z behrisch $

Tutorial for traffic light control via the TraCI interface.

SUMO, Simulation of Urban MObility; see http://sumo.dlr.de/
Copyright (C) 2009-2014 DLR/TS, Germany

This file is part of SUMO.
SUMO is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.
"""

import os, sys
import optparse
import subprocess
import random

from sets import Set

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


def run2():
    """execute the TraCI control loop"""
    traci.init(PORT)
    step = 0
    intervalAggregate = 100.0 #120 seconds
    setVehiclesS0_0 = Set()
    setVehiclesS0_1 = Set()
    setVehiclesE0_0 = Set()
    setVehiclesE0_1 = Set()
    totalVehiclesS0_0 = 0
    totalVehiclesS0_1 = 0
    totalVehiclesE0_0 = 0
    totalVehiclesE0_1 = 0
    setTotalVehiclesS = Set()
    setTotalVehiclesE = Set()
    while traci.simulation.getMinExpectedNumber() > 0:
        traci.simulationStep(step * 1000)
        #print "time: " + step + "\t" + "loop value: " + numPriorityVehicles

        setVehiclesS0_0 = updateNumOfCars(setVehiclesS0_0, traci.inductionloop.getVehicleData("myLoopS0_0"))
        setVehiclesS0_1 = updateNumOfCars(setVehiclesS0_1, traci.inductionloop.getVehicleData("myLoopS0_1"))
        setVehiclesE0_0 = updateNumOfCars(setVehiclesE0_0, traci.inductionloop.getVehicleData("myLoopE0_0"))
        setVehiclesE0_1 = updateNumOfCars(setVehiclesE0_1, traci.inductionloop.getVehicleData("myLoopE0_1"))
                      
        if step % intervalAggregate == 0:           
            totalVehiclesS0_0 = totalVehiclesS0_0 + len(setVehiclesS0_0)
            totalVehiclesS0_1 = totalVehiclesS0_1 + len(setVehiclesS0_1)
            totalVehiclesE0_0 = totalVehiclesE0_0 + len(setVehiclesE0_0)
            totalVehiclesE0_1 = totalVehiclesE0_1 + len(setVehiclesE0_1)
            flowStart = (len(setVehiclesS0_0) + len(setVehiclesS0_1))
            flowEnd = (len(setVehiclesE0_0) + len(setVehiclesE0_1))
            totalStart = totalVehiclesS0_0 + totalVehiclesS0_1
            totalEnd = totalVehiclesE0_0 + totalVehiclesE0_1
            print step, flowStart, flowEnd, totalStart, totalEnd
            
            setTotalVehiclesS = setAddAll(setTotalVehiclesS, setVehiclesS0_0)
            setTotalVehiclesS = setAddAll(setTotalVehiclesS, setVehiclesS0_1)
            setTotalVehiclesE = setAddAll(setTotalVehiclesE, setVehiclesE0_0)
            setTotalVehiclesE = setAddAll(setTotalVehiclesE, setVehiclesE0_1)
            setVehiclesS0_0 = Set()
            setVehiclesS0_1 = Set()
            setVehiclesE0_0 = Set()
            setVehiclesE0_1 = Set()
        step += 1
        
    print len(traci.inductionloop.getVehicleData("myLoopS0_0")) + len(traci.inductionloop.getVehicleData("myLoopS0_1"));
    traci.close()
    sys.stdout.flush()

def setAddAll(setTotal, set):
    for data in set:
        setTotal.add(data)
    return setTotal

def updateNumOfCars(set, dataComplex):
    for data in dataComplex:
        set.add(data[0])
    return set
    
def get_options():
    optParser = optparse.OptionParser()
    optParser.add_option("--nogui", action="store_true", default=False, help="run the commandline version of sumo")
    options, args = optParser.parse_args()
    return options


# this is the main entry point of this script
if __name__ == "__main__":
    options = get_options()

    # this script has been called from the command line. It will start sumo as a
    # server, then connect and run
    if options.nogui:
        sumoBinary = checkBinary('sumo')
    else:
        sumoBinary = checkBinary('sumo-gui')

    # first, generate the route file for this simulation
    #generate_routefile()

    # this is the normal way of using traci. sumo is started as a
    # subprocess and then the python script connects and runs
    pathProj = "C:/tools/SUMO/sumo-0.22.0/SingaporeLoop/"
    sumoProcess = subprocess.Popen([sumoBinary, "-c", pathProj + "Singapore.sumocfg", "--tripinfo-output", pathProj+"tripinfo.xml", "--remote-port", str(PORT)], stdout=sys.stdout, stderr=sys.stderr)
    run2()
    sumoProcess.wait()
