library(tidyverse)
args <- commandArgs(TRUE)
if (is.na(args[1]) || is.na(args[2])) {
    print("usage: Rscript tidy.r input.csv output.csv")
    q()
}
print(paste("reading from ", args[1]))
print(paste("writing to ", args[2]))
raw <- read_csv(args[0])
refined <- select(raw, year, month, day, hour, minute, second, cloud, feed, entity, operation, status, duration) %>%
filter(status == 200) %>%
mutate(entityoperation = paste(entity, operation)) %>%
group_by(year, month, day, hour, minute, second, cloud, feed, entityoperation) %>%
summarise(duration = mean(duration)) %>%
spread(key = entityoperation, value = duration) %>%
refined[is.na(refined)] <- 0
write_csv(refined, path=args[1])

