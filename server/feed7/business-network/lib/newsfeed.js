/**
 * friend two participants
 * @param {info.glennengstrand.Friend} tx 
 * @transaction
 */
async function friendParticipants(tx) {

    tx.from.friends.push(tx.to);
    tx.to.friends.push(tx.from);

    const participantRegistry = await getParticipantRegistry('info.glennengstrand.Broadcaster');
    await participantRegistry.update(tx.from);
	await participantRegistry.update(tx.to);
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
    var o = factory.newResource('info.glennengstrand', 'Outbound', ok);
  	o.created = now;
	o.subject = tx.subject;
	o.story = tx.story;
	o.sender = tx.sender;
	await outboundRegistry.add(o);
    const inboundRegistry = await getAssetRegistry('info.glennengstrand.Inbound');
    for (i = 0; i < tx.sender.friends.length; i++) {
    	const friend = tx.sender.friends[i];
        const ik = 'Inbound:' + k + Math.random();
        var inb = factory.newResource('info.glennengstrand', 'Inbound', ik);
      	inb.created = now;
		inb.subject = tx.subject;
		inb.story = tx.story;
		inb.recipient = friend;
		await inboundRegistry.add(inb);
	}
}
