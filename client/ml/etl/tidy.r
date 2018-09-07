library(tidyverse)
args <- commandArgs(TRUE)
if (is.na(args[1]) || is.na(args[2])) {
    print("usage: Rscript tidy.r input.csv output.csv")
    q()
}
print(paste("reading from ", args[1]))
print(paste("writing to ", args[2]))
raw <- read_csv(args[1])
r1 <- select(raw, year, month, day, hour, minute, second, cloud, feed, entity, operation, status, duration) 
r2 <- filter(r1, status == 200) 
r3 <- mutate(r2, entityoperation = paste(entity, operation))
r4 <- group_by(r3, year, month, day, hour, minute, second, cloud, feed, entityoperation)
r5 <- summarise(r4, duration = mean(duration)) 
r6 <- spread(r5, key = entityoperation, value = duration) 
r6[is.na(r6)] <- 0
write_csv(r6, path=args[2])

