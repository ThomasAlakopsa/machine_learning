board = [
            [0,0,0,0,2,4,3,0,7],
            [9,0,0,0,6,0,0,0,0],
            [7,0,0,0,0,0,0,0,0],
            [0,0,0,5,0,0,7,9,4],
            [0,6,5,0,0,0,0,0,0],
            [0,0,0,8,0,3,0,0,0],
            [0,5,4,0,0,0,0,6,0],
            [0,0,0,0,0,0,0,5,9],
            [0,0,3,4,0,8,0,0,0]
        ]

""" print the board in the familiar sudoku layout """

def print_board(board):
    for i in range(len(board)):
        if i % 3 == 0 and i != 0:
            print("-----------------------------------")

        for j in range(len(board[0])):

            if j % 3 == 0 and j != 0:
                print("|", end=" ")
            if j == 8:
                print(board[i][j], end="\n")
            else:
                print(str(board[i][j])+ " ", end=" ")

"""
find the next not filled in location on the board,
and return the row and collum
"""

def find_clear_spot(board):
    for i in range(len(board)):
        for j in range(len(board[0])):
            if board[i][j] == 0:
                return (i, j)


""" check if the wanted move is a valid move """

def check_valid(board, row, col, num):
    if not check_row(board, row, num):
        return False
    if not check_col(board, row, col, num):
        return False
    if not check_square(board, row, col, num):
        return False
    else:
        return True


""" check if the move is valid accourding too the collum """

def check_col(board, row, col, num):
    for i in range(len(board)):
        if board[i][col] == num and row != i:
            return False
    return True


""" check if the move is valid accourding too the row"""

def check_row(board, row, num):
    for i in range(len(board[row])):
        if board[row][i] == num:
            return False
    return True


""" check if the move is valid in the square """

def check_square(board, row, col, num):

    square_x = row // 3
    square_y = col // 3

    for i in range(square_x*3, square_x*3+3):
        for j in range(square_y*3, square_y*3+3):
            if board[i][j] == num and row != i and col != j:
                return False
    return True



""" main solve function """

def solve(board):
    find = find_clear_spot(board)
    if find:
        row, col = find
    else:
        print_board(board)
        return True

    for i in range(1,10):
        if check_valid(board, row, col, i):
            board[row][col] = i
            if solve(board):
                return True

            board[row][col] = 0

    return False


print("unsolved: ")
print_board(board)
print("solved")
solve(board)

