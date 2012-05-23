def date = new Date()
Calendar c = new GregorianCalendar()
c.setTime(date)
c.set(Calendar.DAY_OF_WEEK,2)

c.getti

println c.format("yyyy-MM-dd")