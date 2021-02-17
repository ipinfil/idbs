package rdg;

import main.DbContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * @author Filip Lajcin
 * This class manages finding cards in database.
 */
public class CardFinder extends Finder<Card> {
    private static final CardFinder INSTANCE = new CardFinder();

    public static CardFinder getInstance() {
        return INSTANCE;
    }

    private CardFinder() {
    }

    /**
     *Finds the card of the account and returns a Card class instance of this card.
     */
    public Card findByAccountId(int accountId) throws SQLException {
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement("SELECT * FROM cards WHERE account_id = ?")) {
            ps.setInt(1, accountId);
            return loadOne(ps);
        }
    }

    @Override
    Card load(ResultSet rs) throws SQLException {
        Card c = new Card();
        c.setAccountId(rs.getInt("account_id"));
        c.setId(rs.getInt("id"));
        c.setBlocked(rs.getBoolean("blocked"));

        return c;
    }
}
