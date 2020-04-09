Private Sub CommandButton1_Click()

    Call Workbooks.Open("R:\1.xlsx")
    'ThisWorkbook.Activate
    Call hogehoge

End Sub

Sub hogehoge()
    With Application.DefaultWebOptions
     .CheckIfOfficeIsHTMLEditor = False
     .RelyOnVML = False
     .AllowPNG = False
     .PixelsPerInch = 96
    End With

    ActiveWorkbook.PublishObjects.Add _
        SourceType:=xlSourceSheet, _
        Filename:="r:\111.html", _
        Sheet:="Sheet1", _
        Source:="", _
        HtmlType:=xlHtmlStatic
    ActiveWorkbook.PublishObjects(1).Publish
    ActiveWorkbook.Close savechanges:=False
End Sub
