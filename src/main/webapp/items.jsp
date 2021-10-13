<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
            integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <title>ToDo list</title>

    <script>
        function validate() {
            let result = true;
            if ($('#description').val().length > 255) {
                alert(`Слишком длинное описание, максимум 255 символов`);
                result = false;
            }
            return result;
        }

        function fillInTable() {
            let newBodyTable = "";
            let options = {
                year: '2-digit',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                hour12: false,
                minute: '2-digit',
                second: '2-digit',
                timezone: 'UTC'
            };
            $.ajax({
                type: 'POST',
                url: 'http://localhost:8080/job4j_todo/fillintable',
                data: 'showAll=' + $('#showAllTasks').is(':checked'),
                dataType: 'json',
                success: function (data) {
                    for (let i = 0; i < data.length; i++) {
                        let dataId = String(data[i]['id']);
                        let dataCreated = new Date(data[i]['created']);
                        let dataDesc = String(data[i]['descItem']);
                        let dataDone = Boolean(data[i]['done']);
                        let user = data[i]['user'];
                        let cats = data[i]['categories'];
                        let categories = [...new Set(cats.map(category => category.name))];

                        newBodyTable += '<tr><td>'
                            + dataId + '</td><td>'
                            + dataDesc + '</td><td>'
                            + dataCreated.toLocaleString("ru", options) + '</td><td>'
                            + user.name + '</td><td>'
                            + categories + '</td><td>'
                            + '<div class="form-check"> <input type="checkbox" id=' + dataId + ' class="form-check-input" onchange="changeTaskStatus(this.id, this.checked)"';
                        if (dataDone) {
                            newBodyTable += ' checked/></div></td></tr>';
                        } else {
                            newBodyTable += '/></div></td></tr>';
                        }
                    }
                    console.log(newBodyTable);
                    document.getElementById('myTableBody').innerHTML = newBodyTable;
                }
            });
        }

        function changeTaskStatus(id, done) {
            console.log('changeTaskStatus: id = ' + id + ', done = ' + done);
            $.ajax({
                type: "POST",
                url: "http://localhost:8080/job4j_todo/item.chg",
                dataType: "json",
                data: {"id": id, "done": done},
                success: function (data) {
                    fillInTable();
                }
            })
        }

        $(document).ready(
            fillInTable
        )
    </script>
</head>
<body>
<div class="container pt-3">
    <div class="container-fluid">
        <h3>Добавить задание</h3>
        <form class="form-horizontal" action="<c:url value='/item.add'/>" method="POST">
            <div class="form-group">
                <label class="control-label col-sm-2" for="description">Описание задания:</label>
                <div class="col-sm-10">
                    <textarea id="description" name="description" class="form-control" rows="3" placeholder="Enter description" required></textarea>
                </div>
            </div>
            <div class="form-group row">
                <label class="col-form-label col-sm-3" for="cIds" style="font-weight: 900">Список категорий</label>
                <div class="col-sm-5">
                    <select class="form-control" name="cIds" id="cIds" multiple required>
                        <c:forEach items="${allCategories}" var="category">
                            <option value='<c:out value="${category.id}"/>'>${category.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn btn-primary" onclick="validate()">Добавить задание</button>
                </div>
            </div>
        </form>
    </div>

    <div class="row">
        <div class="card" style="width: 100%">
            <div class="card-header">
                Задания
            </div>
            <div class="card-body">
                <div class="form-check">
                    <input type="checkbox" class="form-check-input" id="showAllTasks" name="showAllTasks" onclick="fillInTable();">
                    <label class="form-check-label" for="showAllTasks">Показать все задания</label>
                </div>
                <table id="myTable" class="table">
                    <thead>
                    <tr>
                        <th scope="col"># задания</th>
                        <th scope="col">Описание</th>
                        <th scope="col">Дата создания</th>
                        <th scope="col">Пользователь</th>
                        <th scope="col">Категории</th>
                        <th scope="col">Выполнено</th>
                    </tr>
                    </thead>
                    <tbody id="myTableBody">
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>