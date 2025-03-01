import random
from random import randint
import matplotlib.pyplot as plt

# SEED VALUE
# RANDOM NUMBER = randint(1, 0x7FFFFFFF)
X0 = randint(1, 0x7FFFFFFF)

RANGE_START = 0
RANGE_END = 10000


### COMMON LCG SETS
# RANDU = 65539, 0, 2**31
# NUMERICAL RECIPES = 1664525, 1013904223, 2**32
# BORLAND = 22695477, 1, 2**32

# MULTIPLICATIVE FACTOR
A = 65539

# ADDITIVE FACTOR
C = 0

# MODULUS FACTOR
M = 2**31

class LcgRandom(random.Random):
    def __init__(self, seed=[]):
        try:
            self.seed(seed)
        except TypeError:  # not hashable
            self._state = 1

    def seed(self, x):
        self._state = hash(x) % M

    def getstate(self):
        return self._state

    def setstate(self, state):
        self._state = state

    def random(self):
        self._state = ((A * self._state) + C) % M
        return self._state

generator = LcgRandom(X0)

x = []
y = []
z = []

for i in range(RANGE_START, RANGE_END):
    x.append(generator.random())
    y.append(generator.random())
    z.append(generator.random())

fig = plt.figure(figsize=(10,7.5))
plt.suptitle(f'LCG function (A={A}, C={C}, M={M})')
ax = fig.add_axes([0,0,1,1], projection='3d') # use 3d plotting

plt.title(f'LCG function (A={A}, C={C}, M={M})')
#----------------------------layout-------------------------------------
plt.style.use('ggplot')
fig.set_facecolor('w')
plt.subplots_adjust(left=0.15, right=0.9, top=0.85, bottom=0.15)
plt.xticks(fontname='Segoe UI', fontsize=12)
plt.yticks(fontname='Segoe UI', fontsize=12)
#-----------------------------------------------------------------------

ax.plot(x,y,z,'.', color='#927493')
ax.view_init(90, -90) # set initial view
plt.draw()
plt.pause(2) # wait 2 secs, then start rotating

for angle in range(45, 110): # rotate the view
    ax.view_init(angle,-45)
    plt.draw()
    plt.pause(.01)

plt.pause(2)
ax.view_init(45,-45) # set initial view
plt.show()