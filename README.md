# Cache-Simulator
A simple cache simulator which only supports single-address read/write instructions.

## Usage
```shell
javac -encoding GB2312 CCacheSim.java
java CCache
```

## test file
Each test file consists of several lines of instructions in the format of "type address".
`type` = 0 -> read data
`type` = 1 -> write data
`type` = 2 -> read instruction
`address` => address of the data or instruction in memory.

## Prefetch
If you choose `prefetch when cache miss`, the simulator will automatedly load the data or instruction in the next block 
to cache when read instruction/date miss. If prefetch misses, it will increment miss time in summary.

When execute simulation by step, each prefetch process is specified as a step.

## Others
For now, only united Cache is supported (von-Neumann). Maybe update Harvard Structure support later.

LRU, FIFO and Random strategies are supported.

`Write allocate` and `Non-write allocate` are supported.

`write through` method and `write back` method are both supported. But the results of these methods are the same, because 
there are no simulation with respect to memory.

Manually input `type address` instruction is not supported yet.
