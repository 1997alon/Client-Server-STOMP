#pragma once
#include <string>
#include <map>

class Subscriptions
{
private:
    std::map<int, std::string> pending_games;
    std::map<int, std::string> active_games;
    std::map<std::string, int> active_games_bystring;
public:
    Subscriptions();
	virtual ~Subscriptions();
    void addPendSubscription(std::string game, int id);
    void activateSubscription(int id);
    void removeSubscription(int id);
    void clear();
    bool contains(std::string);
    int getIdbyGame(std::string game);
    std::string getGamebyId(int id);
};