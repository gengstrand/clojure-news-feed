using newsfeed.Interfaces;
using newsfeed.Services;
using newsfeed.DAOs;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddScoped<IParticipantService, ParticipantService>();
builder.Services.AddScoped<IParticipantDao, ParticipantDao>();
builder.Services.AddScoped<IOutboundService, OutboundService>();
builder.Services.AddScoped<IOutboundDao, OutboundDao>();
builder.Services.AddScoped<IInboundDao, InboundDao>();
builder.Services.AddScoped<IFriendDao, FriendDao>();
builder.Services.AddScoped<ICacheDao, CacheDao>();
builder.Services.AddScoped<ISearchDao, SearchDao>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// app.UseHttpsRedirection();

// app.UseAuthorization();

app.MapControllers();

app.Run();
