package it.tika

def attrs = "android:layout_marginRight=\"10dip\" android:layout_centerVertical=\"true\" android:layout_alignParentRight=\"true\" android:layout_height=\"wrap_content\" android:layout_width=\"wrap_content\""

attrs.split(" ").each{
    if(it)
    {
        def attr = it.split("=")
        def name = attr[0]
        def value = attr[1][1..-2]
        
        println "<item name=\"$name\">$value</item>"
    }
    
}