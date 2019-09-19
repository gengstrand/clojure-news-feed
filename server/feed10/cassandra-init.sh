cat >/import.cql <<EOF
CREATE keyspace IF NOT EXISTS activity with replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
use activity;

create table IF NOT EXISTS Inbound (
ParticipantID int,
Occurred timeuuid,
FromParticipantID int,
Subject text,
Story text,
primary key (ParticipantID, Occurred));

create table IF NOT EXISTS Outbound (
ParticipantID int,
Occurred timeuuid,
Subject text,
Story text,
primary key (ParticipantID, Occurred));
EOF

# You may add some other conditionals that fits your stuation here
until cqlsh -f /import.cql; do
  echo "cqlsh: Cassandra is unavailable to initialize - will retry later"
  sleep 2
done &

exec /docker-entrypoint.sh "$@"