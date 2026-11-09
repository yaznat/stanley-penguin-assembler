# Stanley/Penguin Ports

The Stanley/Penguin supports up to **256** ports with designated functionalities.

**Under development:** ports will continue to be added.

## Port Specification

Ports have default **8-bit** IDs, listed below.

Ports can also be reconfigured to custom IDs. This allows students in [CMSI 2210](https://bjohnson.lmu.build/cmsi2210web) to properly test their code, as the course specifies varying port numbers.

Specification example:

```bash
stanley nest program.peng --map_ports "PRINT_RAW=1 FILE_REQUEST=0xFF"
```

Unspecified ports will retain their default IDs unless overwritten, in which case they will take the next free ID in ascending order.

## Ports

### 0x00 - PRINT_RAW

**Accepts:** WRITE

**Functionality:** Prints accumulator value as an integer, then flushes the line.

### 0x01 - PRINT_ASCII

**Accepts:** WRITE

**Functionality:** Prints accumulator value as ASCII. Does not flush.

### 0x0A - FILE_REQUEST

**Accepts:** WRITE

**Functionality:** Following a structured request, loads a raw text file (.txt, .csv, etc.) into memory. 

**Request Format:**

```asm
request_addr: req_0

       req_0: str_ptr     ; file path (pointer to null-terminated string)
       req_1: 100         ; destination address (file content will start here)
       req_2: 0           ; status (written by Java -> 0 = ok, 1 = error)
       req_3: 0           ; number of words loaded (written by Java)
```

**Then:**

```asm
              LOAD   request_addr
              WRITE  0x0A
```

**Note:** this implementation is inefficient: it loads UTF-8 bytes into individual 4-byte words. This is subject to change in a future version.