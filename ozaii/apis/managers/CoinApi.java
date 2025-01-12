package ozaii.apis.managers;


import ozaii.managers.CoinManager;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class CoinApi {
    private final CoinManager coinManager;
    private static final Logger logger = Logger.getLogger(CoinApi.class.getName());

    public CoinApi(CoinManager coinManager) {
        this.coinManager = coinManager;
    }

    /**
     * Creates a new bank account for a player.
     *
     * @param playerName The name of the player.
     */
    public void createAccount(String playerName) {
        coinManager.createBankAccount(playerName)
                .thenRun(() -> logger.info("Account created for player: " + playerName))
                .exceptionally(ex -> {
                    logger.severe("Failed to create account for player " + playerName + ": " + ex.getMessage());
                    return null;
                });
    }

    /**
     * Deposits coins into a player's account.
     *
     * @param playerName The name of the player.
     * @param amount     The amount to deposit.
     */
    public void depositCoins(String playerName, double amount) {
        coinManager.deposit(playerName, amount)
                .thenRun(() -> logger.info(amount + " coins deposited to " + playerName))
                .exceptionally(ex -> {
                    logger.severe("Failed to deposit coins for " + playerName + ": " + ex.getMessage());
                    return null;
                });
    }

    /**
     * Withdraws coins from a player's account.
     *
     * @param playerName The name of the player.
     * @param amount     The amount to withdraw.
     */
    public void withdrawCoins(String playerName, double amount) {
        coinManager.remove(playerName, amount)
                .thenRun(() -> logger.info(amount + " coins withdrawn from " + playerName))
                .exceptionally(ex -> {
                    logger.severe("Failed to withdraw coins for " + playerName + ": " + ex.getMessage());
                    return null;
                });
    }

    /**
     * Transfers coins from one player to another.
     *
     * @param sender The name of the player sending the coins.
     * @param taker  The name of the player receiving the coins.
     * @param amount The amount to transfer.
     */
    public void transferCoins(String sender, String taker, double amount) {
        coinManager.transferAccount(sender, taker, amount)
                .thenRun(() -> logger.info("Transferred " + amount + " coins from " + sender + " to " + taker))
                .exceptionally(ex -> {
                    logger.severe("Failed to transfer coins from " + sender + " to " + taker + ": " + ex.getMessage());
                    return null;
                });
    }

    /**
     * Gets the current coin balance for a player.
     *
     * @param playerName The name of the player.
     * @return A future containing the coin balance.
     */
    public CompletableFuture<Double> getBalance(String playerName) {
        return coinManager.getCoins(playerName)
                .thenApply(balance -> {
                    logger.info("Balance for " + playerName + ": " + balance);
                    return balance;
                })
                .exceptionally(ex -> {
                    logger.severe("Failed to fetch balance for " + playerName + ": " + ex.getMessage());
                    return 0.0;
                });
    }

    /**
     * Deletes a player's bank account.
     *
     * @param playerName The name of the player.
     */
    public void deleteAccount(String playerName) {
        coinManager.deleteAccount(playerName)
                .thenRun(() -> logger.info("Deleted account for player: " + playerName))
                .exceptionally(ex -> {
                    logger.severe("Failed to delete account for " + playerName + ": " + ex.getMessage());
                    return null;
                });
    }

    /**
     * Resets a player's coin balance to zero.
     *
     * @param playerName The name of the player.
     */
    public void resetCoins(String playerName) {
        coinManager.resetCoins(playerName)
                .thenRun(() -> logger.info("Reset coins for player: " + playerName))
                .exceptionally(ex -> {
                    logger.severe("Failed to reset coins for " + playerName + ": " + ex.getMessage());
                    return null;
                });
    }
}
