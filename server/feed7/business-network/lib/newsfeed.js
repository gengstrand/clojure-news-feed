/**
 * friend two participants
 * @param {info.glennengstrand.Friend} tx 
 * @transaction
 */
async function friendParticipants(tx) {
    const created = Date.now();
    const factory = getFactory();
    const friendRegistry = await getAssetRegistry('info.glennengstrand.Friendship');
    const kf = tx.from.participantId + '|' + created + '|';
    const fkf = 'Friend:' + kf + Math.random();
    var ff = factory.newResource('info.glennengstrand', 'Friendship', fkf);
    ff.from = tx.from;
    ff.to = tx.to;
    await friendRegistry.add(ff);
    const kt = tx.to.participantId + '|' + created + '|';
    const fkt = 'Friend:' + kt + Math.random();
    var ft = factory.newResource('info.glennengstrand', 'Friendship', fkt);
    ft.from = tx.to;
    ft.to = tx.from;
    await friendRegistry.add(ft);
}

/**
 * broadcast a news item to the sender's friends
 * @param {info.glennengstrand.Broadcast} tx 
 * @transaction
 */
async function broadcastParticipants(tx) {
    const factory = getFactory();
    const created = Date.now();
    const now = new Date();
    const k = tx.sender.participantId + '|' + created + '|';
    const outboundRegistry = await getAssetRegistry('info.glennengstrand.Outbound');
    const ok = 'Outbound:' + k + Math.random();
    const inboundRegistry = await getAssetRegistry('info.glennengstrand.Inbound');
    var o = factory.newResource('info.glennengstrand', 'Outbound', ok);
    o.created = now;
    o.subject = tx.subject;
    o.story = tx.story;
    o.sender = tx.sender;
    await outboundRegistry.add(o);
    const friends = await query('broadcasterFriends', { broadcaster: 'resource:info.glennengstrand.Broadcaster#' + tx.sender.participantId });
    for (i = 0; i < friends.length; i++) {
    	const friend = friends[i];
        const ik = 'Inbound:' + k + Math.random();
        var inb = factory.newResource('info.glennengstrand', 'Inbound', ik);
      	inb.created = now;
	inb.subject = tx.subject;
	inb.story = tx.story;
	inb.recipient = friend.to;
	await inboundRegistry.add(inb);
    }
}
