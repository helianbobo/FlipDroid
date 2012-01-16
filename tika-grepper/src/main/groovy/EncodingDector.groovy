def file1 = new File('c:/big.txt')
def file2 = new File('c:/small.txt')

def m1 = [:]

file1.eachLine {
    m1.put(it, 0)
}

file2.eachLine {
    if (m1.get(it)!=null) {
        m1.put(it, m1[it] + 1)
    }
}

def m2 = m1.findAll {k, v ->
    return v == 1
}
println m2.size()