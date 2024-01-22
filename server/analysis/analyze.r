library(tidyverse)
raw15 <- read_csv("feed15perf.csv")
r15 <- select(raw15, hour, minute, entity, duration, status) %>%
     filter(status == 200) %>%
     group_by(hour, minute, entity) %>%
     count(hour, minute, entity) %>%
     spread(key = entity, value = n)
summary(r15)

l15 <- select(raw15, hour, minute, entity, duration, status) %>%
     filter(status == 200) %>%
     group_by(hour, minute, entity) %>%
     summarise(duration = mean(duration)) %>%
     spread(key = entity, value = duration)
summary(l15)

raw14 <- read_csv("feed14perf.csv")
r14 <- select(raw14, hour, minute, entity, duration, status) %>%
     filter(status == 200) %>%
     count(hour, minute, entity) %>%
     spread(key = entity, value = n)
summary(r14)

raw15 <- read_csv("pfo15.csv")
g15 <- gather(raw15, entity, rpm, -timestamp)
ggplot(g15, aes(x = timestamp, y = rpm, group=entity, colour=entity)) + theme_minimal() + theme(legend.position = "bottom") + labs(title = "feed 15 (kotlin on Web Flux) throughput", x = "minute", y = "RPM") + geom_line()

raw14 <- read_csv("pfo14.csv")
g14 <- gather(raw14, entity, rpm, -timestamp)
ggplot(g14, aes(x = timestamp, y = rpm, group=entity, colour=entity)) + theme_minimal() + theme(legend.position = "bottom") + labs(title = "feed 14 (csharp on ASP.NET) throughput", x = "minute", y = "RPM") + geom_line()

raw8 <- read_csv("pfo8.csv")
g8 <- gather(raw8, entity, rpm, -timestamp)
ggplot(g8, aes(x = timestamp, y = rpm, group=entity, colour=entity)) + theme_minimal() + theme(legend.position = "bottom") + labs(title = "feed 8 (java on spring boot) throughput", x = "minute", y = "RPM") + geom_line()

raw <- read_csv("feedgke.csv")

ggplot(raw, aes(x = feed, y = rpm, fill = entity)) + geom_bar(position = "dodge", stat = "identity")

ggplot(raw, aes(x = feed, y = avg_duration, fill = entity)) + geom_bar(position = "dodge", stat = "identity")

ggplot(raw, aes(x = feed, y = p95, fill = entity)) + geom_bar(position = "dodge", stat = "identity")

sca_raw <- read_csv("count-file-types.csv")

sca <- filter(sca_raw, count > 10) %>% gather(metric, count, -type)

ggplot(sca, aes(x = type, y = count, group=metric, fill=metric)) + geom_bar(stat = "identity")

