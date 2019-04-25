package me.idarkyy.dbc.ticketmanager;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketManager {
    private static final Pattern pattern = Pattern.compile("(.*+)-(\\d{4})");

    // The parent guild of the manager
    private Guild guild;

    private @NotNull Category ticketsCategory; // The category where new tickets will be placed at
    private @Nullable Category closedTicketsCategory; // The category where closed tickets will be placed at

    // The current ticket number
    // Can be saved an re-set using the #setCurrentNumber method
    private int currentNumber = 0;

    private Permission[] allowedPermissions; // Permissions of the ticket "creator" (AKA the member parameter)
    private Permission[] roleAllowedPermissions; // Permissions of the above-access roles

    /**
     * Constructs a new Ticket Manager for the specified guild
     * @param guild The parent of the manager
     * @param ticketsCategory (Should not be null) The category where new tickets will be placed at
     * @param closedTicketsCategory (Not null) The category where closed tickets will go before the final close
     */
    public TicketManager(Guild guild, @NotNull Category ticketsCategory, @Nullable Category closedTicketsCategory) {
        this.guild = guild;

        this.ticketsCategory = ticketsCategory;
        this.closedTicketsCategory = closedTicketsCategory;

        setInitialPermissions();
    }

    /**
     * Constructs a new Ticket Manager for the specified guild
     * Creates new categories if they don't exist
     * @param guild The parent of the manager
     * @param useClosedTickets Should the Closed Tickets category be used / made
     */
    public TicketManager(Guild guild, boolean useClosedTickets) {
        this.guild = guild;

        if (guild.getCategoriesByName("Tickets", true).size() == 0) {
            ticketsCategory = (Category) guild.getController().createCategory("Tickets").complete();
        }

        if (useClosedTickets && guild.getCategoriesByName("Closed Tickets", true).size() == 0) {
            closedTicketsCategory = (Category) guild.getController().createCategory("Closed Tickets").complete();
        }

        setInitialPermissions();
    }

    /**
     * Creates a ticket
     * @param member The member (usually the ticket creator)
     * @param rolesWithAccess Roles with special access to the ticket
     * @return The ticket channel
     */

    public TextChannel createTicket(String name, Member member, Role... rolesWithAccess) {
        TextChannel tc = (TextChannel) ticketsCategory.createTextChannel(name.replace(" ", "-") + "-" + asFourDigitString(nextNumber())).complete();

        tc.createPermissionOverride(member).setAllow(allowedPermissions).queue();

        for (Role r : rolesWithAccess) {
            tc.createPermissionOverride(r).setAllow(roleAllowedPermissions).queue();
        }

        return tc;
    }

    public TextChannel createTicket(Member member, Role... rolesWithAccess) {
        return createTicket("ticket", member, rolesWithAccess);
    }


    /**
     * Closes the specified Ticket
     *
     * @param textChannel The ticket
     * @return Is the close final (is the channel deleted or moved to the Closed Tickets category)
     */
    public boolean closeTicket(TextChannel textChannel) {
        if (!textChannel.getName().matches(pattern.pattern())) {
            throw new IllegalArgumentException("Target channel is not a ticket");
        }

        if (closedTicketsCategory == null || closedTicketsCategory.getChannels().contains(textChannel)) {
            textChannel.delete().queue();
            return true;
        }

        if(closedTicketsCategory != null && !closedTicketsCategory.getChannels().contains(textChannel)) {
            textChannel.getManager().setParent(closedTicketsCategory).queue();
            return false;
        }

        return false;
    }

    /**
     * Closes the specified ticket
     * @param name The ticket name
     */
    public HashMap<TextChannel, Boolean> closeTickets(String name) {
        HashMap<TextChannel, Boolean> after = new HashMap<>();

        findTicketsByName(name).forEach(tc -> after.put(tc, closeTicket(tc)));

        return after;
    }


    /**
     * Closes the specified ticket
     * @param id The ticket int id (without zeros - not 0042, but 42)
     */
    public boolean closeTicket(int id) {
        TextChannel tc = findTicketById(id);

        if (tc == null) {
            throw new IllegalArgumentException("Ticket with ID " + id + " not found in guild with ID " + guild.getId());
        }

        return closeTicket(tc);
    }

    public void renameTicket(TextChannel textChannel, String name) {
        name = name.toLowerCase().replace(" ", "-");

        String id = getTicketIdString(textChannel);

        textChannel.getManager().setName(name + "-" + id).queue();
    }

    public void renameTickets(String ticketName, String newName) {
        findTicketsByName(ticketName).forEach(tc -> renameTicket(tc, newName));
    }

    public void renameTicket(int ticketId, String name) {
        TextChannel tc = findTicketById(ticketId);

        if (tc == null) {
            throw new IllegalArgumentException("Ticket with ID " + ticketId + " not found in guild with ID " + guild.getId());
        }

        renameTicket(tc, name);
    }

    /**
     * Returns the ticket name
     * @param textChannel The ticket
     * @return The ticket name
     */
    public String getTicketName(TextChannel textChannel) {
        Matcher matcher = pattern.matcher(textChannel.getName());

        if(matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Invalid ticket name: " + textChannel.getName());
        }
    }

    public TextChannel findTicket(TextChannel textChannel) {
        Matcher matcher = pattern.matcher(textChannel.getName());

        if (matcher.find()) {
            return textChannel;
        }

        return null;
    }

    public List<TextChannel> findTicketsByName(String name) {
        List<TextChannel> textChannels = new ArrayList<>();

        for (TextChannel tc : ticketsCategory.getTextChannels()) {
            if (getTicketName(tc).equalsIgnoreCase(name)) {
                textChannels.add(tc);
            }
        }

        return textChannels;
    }

    public TextChannel findTicketById(int id) {
        for (TextChannel tc : ticketsCategory.getTextChannels()) {
            if (getTicketId(tc) == id) {
                return tc;
            }
        }

        return null;
    }

    /**
     * Returns the ticket ID
     * @param textChannel The ticket
     * @return The ticket ID with zeros before it (string value)
     */
    public String getTicketIdString(TextChannel textChannel) {
        Matcher matcher = pattern.matcher(textChannel.getName());

        if(matcher.find()) {
            return matcher.group(2);
        } else {
            throw new IllegalArgumentException("Invalid ticket name: " + textChannel.getName());
        }
    }

    /**
     * Returns the ticket ID
     * @param textChannel The ticket
     * @return The ticket ID without zeros before it (int value)
     */
    public int getTicketId(TextChannel textChannel) {
        return Integer.parseInt(getTicketIdString(textChannel));
    }

    /**
     * Next ticket number
     * @return The next ticket number
     */
    public int nextNumber() {
        return currentNumber++;
    }

    /**
     * Returns the four-digit value of an integer
     * If the integer is 3, returns 0003
     * If the integer is 42, returns 0042
     * If the integer is 953, returns 0953
     * If above four digits, it might break
     * @param number The number
     * @return The string value of the integer with four digits
     */
    public String asFourDigitString(int number) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i >= (4 - String.valueOf(number).length()); i++) {
            sb.append("0");

        }

        return sb.append(number).toString();
    }

    /**
     * Resets the permissions and sets them to the initial ones
     */
    public void setInitialPermissions() {
        setAllowedPermissions(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_ADD_REACTION, Permission.VIEW_CHANNEL);
        setRoleAllowedPermissions(
                /* member permissions */ Permission.MESSAGE_WRITE, Permission.MESSAGE_READ, Permission.MESSAGE_ADD_REACTION, Permission.VIEW_CHANNEL,
                /* staff permissions */ Permission.MESSAGE_MANAGE, Permission.MANAGE_CHANNEL);

    }

    /**
     * Allowed permissions of the ticket creator (AKA the member parameter)
     * @return Permission array
     */
    public Permission[] getAllowedPermissions() {
        return allowedPermissions;
    }

    /**
     * Sets the allowed permissions of the ticket creator (AKA the member parameter)
     * @param allowedPermissions Permission array
     */
    public void setAllowedPermissions(Permission... allowedPermissions) {
        this.allowedPermissions = allowedPermissions;
    }

    /**
     * Allowed permission of the allowed roles in the ticket
     * @return Permission array
     */
    public Permission[] getRoleAllowedPermissions() {
        return roleAllowedPermissions;
    }


    /**
     * Sets the allowed permission of the allowed roles in the ticket
     * @param roleAllowedPermissions Permission array
     */
    public void setRoleAllowedPermissions(Permission... roleAllowedPermissions) {
        this.roleAllowedPermissions = roleAllowedPermissions;
    }

    /**
     * Current ticket number
     * @return the current ticket number value
     */
    public int getCurrentNumber() {
        return currentNumber;
    }

    /**
     * Updates the current ticket number
     * @param currentNumber The number to set
     */
    public void setCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
    }

    /**
     * Category used to store new (non-closed) tickets
     * @return The ticket category
     */
    @NotNull
    public Category getTicketsCategory() {
        return ticketsCategory;
    }

    /**
     * Sets the ticket category
     * @param ticketsCategory the category
     */
    public void setTicketsCategory(@NotNull Category ticketsCategory) {
        this.ticketsCategory = ticketsCategory;
    }

    /**
     * Nullable category used to store closed tickets
     * The tickets are in a pre-final close phase (they close on the next method execution
     * @return the closed tickets category
     */
    @Nullable
    public Category getClosedTicketsCategory() {
        return closedTicketsCategory;
    }

    /**
     * Sets the category used to store closed tickets
     * @param closedTicketsCategory the new closed tickets category
     */
    public void setClosedTicketsCategory(@Nullable Category closedTicketsCategory) {
        this.closedTicketsCategory = closedTicketsCategory;
    }

    /**
     * Parent of the ticket manager
     * @return The parent guild
     */
    public Guild getGuild() {
        return guild;
    }
}
