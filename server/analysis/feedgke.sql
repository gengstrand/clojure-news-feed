select feed, entity, avg(rpm) as rpm, sum(sum_duration) / sum(rpm) as avg_duration, 
APPROX_QUANTILE_DS(quantile_duration, 0.50) as p50,
APPROX_QUANTILE_DS(quantile_duration, 0.95) as p95,
APPROX_QUANTILE_DS(quantile_duration, 0.99) as p99,
avg(max_duration) as max_duration
from feed
where operation = 'POST' and cloud = 'GKE'
group by feed, entity
order by feed, entity
