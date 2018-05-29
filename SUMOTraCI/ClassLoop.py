from sets import Set
import traci

class ClassLoop:
    name = "noname"
    loopNames = []
    setsVehicles = {}
    setsTotal = {}
    counts = []
    nLoops = 0
    flow = 0
    total = 0
    
    timeEntry = dict()    
    def __init__(self, name, loopNames):
        self.name = name
        self.loopNames = loopNames
        self.setsVehicles = [];
        self.setsTotal = [];
        self.counts = [];
        self.nLoops = len(loopNames);
        self.flow = 0
        self.total = 0
        self.timeEntry = dict()
        for loopName in loopNames:
            self.setsVehicles.append(Set())
            self.setsTotal.append(Set())
            self.counts.append(0)
    
    def update(self):
        for n in range(0, self.nLoops):
            loopName = self.loopNames[n]
            dataComplex = traci.inductionloop.getVehicleData(loopName)
            set = self.setsVehicles[n]
            for data in dataComplex:
                set.add(data[0])
                if (self.timeEntry.has_key(data[0]) == False):
                    self.timeEntry[data[0]] = data[2]

                
    def setAddAll(self, setTotal, set):
        for data in set:
            setTotal.add(data)
        return setTotal
        
    def count(self):
        self.flow = 0
        for n in range(0, self.nLoops):
            loopName = self.loopNames[n]
            setVehicles = self.setsVehicles[n]
            setTotal = self.setsTotal[n]
            self.counts[n] = self.counts[n] + len(setVehicles)
            self.flow = self.flow + len(setVehicles)
            self.total = self.total + len(setVehicles)
            print self.name, loopName, self.flow, self.total
            
            self.setAddAll(setTotal, setVehicles)
            self.setsVehicles[n] = Set()